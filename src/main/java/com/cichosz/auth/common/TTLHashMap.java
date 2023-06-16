package com.cichosz.auth.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TTLHashMap<K, V> {
    
    private final Map<K, ValueWithExpiration> map = new HashMap<>();
    private final long defaultTTL;
    private final long cleanupInterval;
    private volatile boolean running = true;

    public TTLHashMap(long defaultTTL, TimeUnit timeUnit, long cleanupInterval, TimeUnit cleanupIntervalTimeUnit) {
        this.defaultTTL = defaultTTL;
        this.cleanupInterval = cleanupInterval;
        startCleanupThread();
    }
    
	public Set<K> keySet(){
		return this.map.keySet();
	}

    public synchronized void put(K key, V value, long ttl, TimeUnit timeUnit) {
        long expirationTime = System.currentTimeMillis() + ttl;
        map.put(key, new ValueWithExpiration(value, expirationTime));
    }

    public synchronized void put(K key, V value) {
        put(key, value, defaultTTL, TimeUnit.MILLISECONDS);
    }

    public synchronized V get(K key) {
        ValueWithExpiration valueWithExpiration = map.get(key);
        if (valueWithExpiration == null) {
            return null;
        }
        if (valueWithExpiration.hasExpired()) {
            map.remove(key);
            return null;
        }
        return valueWithExpiration.getValue();
    }

    private class ValueWithExpiration {
        private final V value;
        private final long expirationTime;

        public ValueWithExpiration(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public V getValue() {
            return value;
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }
    }

    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(cleanupInterval);
                    removeExpiredEntries();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    private synchronized void removeExpiredEntries() {
        long now = System.currentTimeMillis();
        map.entrySet().removeIf(entry -> entry.getValue().expirationTime < now);
    }

    public synchronized void stop() {
        running = false;
    }
}
