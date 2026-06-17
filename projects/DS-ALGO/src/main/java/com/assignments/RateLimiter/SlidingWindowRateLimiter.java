package com.assignments.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// ─────────────────────────────────────────────────────────────────────────────
// Multi-bucket sliding window rate limiter
//
// Design:
//   - windowMs is divided into numBuckets equal sub-buckets
//   - sub-buckets live in a circular (ring) array
//   - on each request, expired buckets are evicted (zeroed + recycled)
//   - effective count = sum of all bucket counts (only live buckets remain)
//   - strict time alignment: new bucket starts at prevStart + bucketMs
//     (not 'now'), preventing boundary drift
//
// Thread safety: per-user limiter uses 'synchronized'; the registry uses
// ConcurrentHashMap so different users never contend on the same lock.
// ─────────────────────────────────────────────────────────────────────────────

public class SlidingWindowRateLimiter {

    // ── Inner class: per-user limiter ─────────────────────────────────────────

    static class UserLimiter {

        private final int    limit;
        private final int    numBuckets;
        private final long   bucketMs;
        private final int[]  counts;
        private final long[] bucketStart;
        private int          head;

        /**
         * @param limit       max requests allowed within the full window
         * @param windowMs    total window size in milliseconds
         * @param numBuckets  how many sub-buckets to divide the window into
         *                    (higher = more granular, less burst leakage)
         */
        UserLimiter(int limit, long windowMs, int numBuckets) {
            if (windowMs % numBuckets != 0) {
                throw new IllegalArgumentException(
                    "windowMs (" + windowMs + ") must be divisible by numBuckets (" + numBuckets + ")"
                );
            }
            this.limit       = limit;
            this.numBuckets  = numBuckets;
            this.bucketMs    = windowMs / numBuckets;
            this.counts      = new int[numBuckets];
            this.bucketStart = new long[numBuckets];

            // initialise all bucket starts to a consistent baseline
            // (rounded down to bucket boundary so first eviction is precise)
            long now  = System.currentTimeMillis();
            long base = (now / bucketMs) * bucketMs;
            for (int i = 0; i < numBuckets; i++) {
                bucketStart[i] = base;
            }
            this.head = 0;
        }

        /**
         * Attempt to pass a single request through the limiter.
         * @return true  → allowed
         *         false → rate limit exceeded
         */
        public synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();
            evictExpired(now);

            int total = 0;
            for (int c : counts) total += c;

            if (total < limit) {
                counts[head]++;
                return true;
            }
            return false;
        }

        /**
         * Returns the number of requests consumed in the current window.
         */
        public synchronized int currentCount() {
            evictExpired(System.currentTimeMillis());
            int total = 0;
            for (int c : counts) total += c;
            return total;
        }

        /**
         * Advance head past any expired buckets, zeroing and recycling each one.
         * Uses strict time alignment: new bucket starts at prevStart + bucketMs,
         * not 'now', so bucket boundaries never drift under load.
         */
        private void evictExpired(long now) {
            while (now - bucketStart[head] >= bucketMs) {
                // the slot head points to has expired; advance to next slot
                int prev    = head;
                head        = (head + 1) % numBuckets;
                counts[head]      = 0;
                // strict alignment: new bucket starts exactly one bucketMs after the old one
                bucketStart[head] = bucketStart[prev] + bucketMs;
            }
        }

        /** How many more requests can pass before the limit is hit. */
        public synchronized int remaining() {
            return Math.max(0, limit - currentCount());
        }

        @Override
        public synchronized String toString() {
            return String.format("UserLimiter[limit=%d, used=%d, remaining=%d, buckets=%d, bucketMs=%dms]",
                limit, currentCount(), remaining(), numBuckets, bucketMs);
        }
    }

    // ── Registry: one UserLimiter per user ───────────────────────────────────

    private final ConcurrentHashMap<String, UserLimiter> registry = new ConcurrentHashMap<>();
    private final int  limit;
    private final long windowMs;
    private final int  numBuckets;

    public SlidingWindowRateLimiter(int limit, long windowMs, int numBuckets) {
        this.limit      = limit;
        this.windowMs   = windowMs;
        this.numBuckets = numBuckets;
    }

    /**
     * Check whether the given userId may proceed.
     * Creates a limiter on first call for a new user.
     */
    public boolean allowRequest(String userId) {
        UserLimiter limiter = registry.putIfAbsent(
            userId, new UserLimiter(limit, windowMs, numBuckets)
        );
        limiter = registry.get(userId);
        return limiter.allowRequest();
    }

    public int remaining(String userId) {
        UserLimiter limiter = registry.get(userId);
        return limiter == null ? limit : limiter.remaining();
    }

    public int currentCount(String userId) {
        UserLimiter limiter = registry.get(userId);
        return limiter == null ? 0 : limiter.currentCount();
    }

    // ── Demo ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {

        // Config: 10 requests per 6 seconds, divided into 6 buckets of 1s each
        final int  LIMIT       = 10;
        final long WINDOW_MS   = 6_000;
        final int  NUM_BUCKETS = 6;

        SlidingWindowRateLimiter rl = new SlidingWindowRateLimiter(LIMIT, WINDOW_MS, NUM_BUCKETS);

        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  Sliding Window Rate Limiter — Java Demo");
        System.out.printf ("  Limit: %d req / %ds   |   %d buckets × %dms each%n",
            LIMIT, WINDOW_MS / 1000, NUM_BUCKETS, WINDOW_MS / NUM_BUCKETS);
        System.out.println("═══════════════════════════════════════════════════\n");

        // ── Scenario 1: burst at startup ─────────────────────────────────────
        System.out.println("── Scenario 1: burst of 14 requests for user-A ──");
        AtomicInteger allowed  = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        for (int i = 1; i <= 14; i++) {
            boolean ok = rl.allowRequest("user-A");
            if (ok) allowed.incrementAndGet(); else rejected.incrementAndGet();
            System.out.printf("  req #%2d → %s  (used=%d, remaining=%d)%n",
                i,
                ok ? "ALLOW " : "REJECT",
                rl.currentCount("user-A"),
                rl.remaining("user-A")
            );
        }
        System.out.printf("%n  Result: %d allowed, %d rejected%n%n", allowed.get(), rejected.get());

        // ── Scenario 2: window slides — requests recover over time ───────────
        System.out.println("── Scenario 2: wait 2s, then retry (window slides) ──");
        System.out.println("  Sleeping 2000ms...");
        Thread.sleep(2_000);

        allowed.set(0); rejected.set(0);
        for (int i = 1; i <= 5; i++) {
            boolean ok = rl.allowRequest("user-A");
            if (ok) allowed.incrementAndGet(); else rejected.incrementAndGet();
            System.out.printf("  req #%2d → %s  (used=%d, remaining=%d)%n",
                i,
                ok ? "ALLOW " : "REJECT",
                rl.currentCount("user-A"),
                rl.remaining("user-A")
            );
        }
        System.out.printf("%n  Result: %d allowed, %d rejected%n%n", allowed.get(), rejected.get());

        // ── Scenario 3: full window expiry ───────────────────────────────────
        System.out.println("── Scenario 3: wait full window (6s), quota fully resets ──");
        System.out.println("  Sleeping 6000ms...");
        Thread.sleep(6_000);

        allowed.set(0); rejected.set(0);
        for (int i = 1; i <= 12; i++) {
            boolean ok = rl.allowRequest("user-A");
            if (ok) allowed.incrementAndGet(); else rejected.incrementAndGet();
            System.out.printf("  req #%2d → %s  (used=%d, remaining=%d)%n",
                i,
                ok ? "ALLOW " : "REJECT",
                rl.currentCount("user-A"),
                rl.remaining("user-A")
            );
        }
        System.out.printf("%n  Result: %d allowed, %d rejected%n%n", allowed.get(), rejected.get());

        // ── Scenario 4: concurrent users don't share state ──────────────────
        System.out.println("── Scenario 4: two independent users ──");
        for (int i = 1; i <= 4; i++) {
            boolean a = rl.allowRequest("user-A");
            boolean b = rl.allowRequest("user-B");
            System.out.printf("  req #%d  user-A=%s (used=%d)  user-B=%s (used=%d)%n",
                i,
                a ? "ALLOW " : "REJECT", rl.currentCount("user-A"),
                b ? "ALLOW " : "REJECT", rl.currentCount("user-B")
            );
        }

        // ── Scenario 5: high-frequency concurrent load ───────────────────────
        System.out.println("\n── Scenario 5: 20 concurrent threads hammering user-C ──");
        SlidingWindowRateLimiter concurrentRl = new SlidingWindowRateLimiter(10, 6_000, 6);

        AtomicInteger concAllowed  = new AtomicInteger();
        AtomicInteger concRejected = new AtomicInteger();
        Thread[] threads = new Thread[20];

        for (int i = 0; i < 20; i++) {
            threads[i] = new Thread(() -> {
                if (concurrentRl.allowRequest("user-C")) {
                    concAllowed.incrementAndGet();
                } else {
                    concRejected.incrementAndGet();
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.printf("  20 concurrent requests → %d allowed, %d rejected%n",
            concAllowed.get(), concRejected.get());
        System.out.println("  (expected: exactly 10 allowed, 10 rejected)\n");

        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  Done.");
        System.out.println("═══════════════════════════════════════════════════");
    }
}