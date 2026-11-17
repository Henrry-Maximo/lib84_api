package com.example.apirestspringboot.idempotency;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

/*
 * ConcurrentHashMap com TTL de 24h. Cada entrada tem: bodyHash (SHA-256), state (IN_PROGRESS ou DONE), status HTTP e
 * body da resposta. O markInProgress usa putIfAbsent para ser atômico e detectar concorrência.
 */

@Component
public class IdempotencyStore {

    private static final long TTL_SECONDS = 86_400; // 24h

    public enum State { IN_PROGRESS, DONE }

    public record Entry(String bodyHash, State state, int status, String body, Instant expiresAt) {}

    private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

    public Entry get(String key) {
        Entry e = store.get(key);

        if (e == null) return null;
        if (Instant.now().isAfter(e.expiresAt())) {
            store.remove(key);
            return null;
        }

        return e;
    }

    // marca a chave como IN_PROGRESS atomicamente. Retorna false se já existir
    public boolean markInProgress(String key, String bodyHash) {
        Entry entry = new Entry(bodyHash, State.IN_PROGRESS, 0, null, Instant.now().plusSeconds(TTL_SECONDS));

        return store.putIfAbsent(key, entry) == null;
    }

    public void markDone(String key, int status, String body) {
        Entry existing = store.get(key);

        if (existing != null) {
            store.put(key, new Entry(existing.bodyHash(), State.DONE, status, body, existing.expiresAt()));
        }
    }

    public void remove(String key) {
        store.remove(key);
    }

    // codificação sha256
    public static String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(digest.digest(data));
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
