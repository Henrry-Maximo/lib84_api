package com.example.apirestspringboot.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimitService {

    // estrutura de dados para armazenar o bucket (chave: valor)
    // chave -> clientKey (identificação do usuário) por IP (anônimo) ou userId (JWT)
    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    // definição de quantidade de tokens por segundo para os planos
    private static final long   PREMIUM_CAPACITY = 100;
    private static final double PREMIUM_REFILL   = 0.5; // meio token por segundo (1 token a cada 2s)
    private static final long   BASIC_CAPACITY   = 50;
    private static final double BASIC_REFILL     = 0.2; // um quinto de token por segundo (1 token a cada 5s)

    public TokenBucket getBucket(String clientKey, boolean isPremium) {
        long capacity = isPremium ? PREMIUM_CAPACITY : BASIC_CAPACITY;
        double refill  = isPremium ? PREMIUM_REFILL   : BASIC_REFILL;

        TokenBucket existing = buckets.get(clientKey);
        // se o bucket existe mas o plano mudou, recria
        if (existing != null && existing.getCapacity() != capacity) {
            buckets.remove(clientKey);
        }

        return buckets.computeIfAbsent(clientKey, k -> new TokenBucket(capacity, refill));
    }
}
