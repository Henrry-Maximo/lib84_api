package com.example.apirestspringboot.service;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public final class TokenBucket {

    private final long capacity;
    private final double refillTokensPerSecond;
    private double availableTokens;
    private Instant lastRefillTimestamp;

    // habilitar gerenciamento ReentrantLock, para travar a entrada de execuções na Thread
    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucket(long capacity, double refillTokensPerSecond) {
        this.capacity = capacity; // capacidade
        this.refillTokensPerSecond = refillTokensPerSecond; // quantidade  por segundo
        this.availableTokens = capacity; // quantidade restante
        this.lastRefillTimestamp = Instant.now(); // última vez executado
    }

    public long tryConsume(long tokens) {
        lock.lock();
        try {
            refill();
            if (availableTokens >= tokens) {
                availableTokens -= tokens;
                return (long) availableTokens; // retorna restante após consumo
            }
            return -1; // sinaliza bloqueio
        } finally {
            lock.unlock();
        }
    }

    public long getAvailableTokens() {
        lock.lock();
        try {
            refill();
            return (long) availableTokens;
        } finally {
            lock.unlock();
        }
    }

    public long getCapacity() {
        return capacity;
    }

    // calculo de quantidade disponível de toknes e última vez executado
    private void refill() {
        Instant now = Instant.now();
        double seconds = (now.toEpochMilli() - lastRefillTimestamp.toEpochMilli()) / 1000.0;
        if (seconds > 0) {
            availableTokens = Math.min(capacity, availableTokens + seconds * refillTokensPerSecond);
            lastRefillTimestamp = now;
        }
    }
}
