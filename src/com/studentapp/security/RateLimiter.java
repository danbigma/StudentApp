package com.studentapp.security;

import com.studentapp.config.AppConfig;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private static final Map<String, Entry> attempts = new ConcurrentHashMap<>();
    private static final AppConfig cfg = AppConfig.get();

    private static class Entry {
        int count;
        long lockedUntilEpochSec;
    }

    private static Entry get(String key) {
        return attempts.computeIfAbsent(key, k -> new Entry());
    }

    public static boolean isBlocked(String key) {
        Entry e = get(key);
        long now = Instant.now().getEpochSecond();
        return e.lockedUntilEpochSec > now;
    }

    public static long secondsUntilUnlock(String key) {
        Entry e = get(key);
        long now = Instant.now().getEpochSecond();
        long diff = e.lockedUntilEpochSec - now;
        return Math.max(0, diff);
    }

    public static void onFailure(String key) {
        Entry e = get(key);
        e.count++;
        if (e.count >= cfg.getLoginMaxAttempts()) {
            e.lockedUntilEpochSec = Instant.now().getEpochSecond() + cfg.getLoginLockoutSeconds();
            e.count = 0; // reset counter after lock
        }
    }

    public static void onSuccess(String key) {
        attempts.remove(key);
    }
}

