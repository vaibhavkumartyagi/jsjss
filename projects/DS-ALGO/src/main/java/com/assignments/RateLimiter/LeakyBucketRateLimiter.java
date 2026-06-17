package com.assignments.RateLimiter;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LeakyBucketRateLimiter {

    private final LinkedBlockingDeque<Runnable> bucket;
    private final ScheduledExecutorService leakExecutor;
    private final int capacity;

    public LeakyBucketRateLimiter(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        // bounded deque — this IS the bucket
        this.bucket = new LinkedBlockingDeque<>(capacity);

        this.leakExecutor = Executors.newSingleThreadScheduledExecutor();

        // leak one request at fixed interval
        long intervalMicros = 1_000_000L / leakRatePerSecond;
        leakExecutor.scheduleAtFixedRate(
            this::leak,
            0,
            intervalMicros,
            TimeUnit.MICROSECONDS
        );
    }

    // called by incoming request thread
    public boolean submitRequest(Runnable request) {
        // offerLast is non-blocking — returns false if bucket full
        boolean accepted = bucket.offerLast(request);

        if (!accepted) {
            System.out.println("Bucket full — request dropped (429)");
        }

        return accepted;
    }

    // called by leak thread at fixed rate
    private void leak() {
        Runnable request = bucket.pollFirst(); // non-blocking
        if (request != null) {
            try {
                request.run(); // process at fixed rate
            } catch (Exception e) {
                System.err.println("Error processing request: " + e.getMessage());
            }
        }
    }

    public void shutdown() {
        leakExecutor.shutdown();
    }

    public int getCurrentLoad() {
        return bucket.size();
    }

    public static void main(String[] args) throws InterruptedException {
        LeakyBucketRateLimiter limiter =
                new LeakyBucketRateLimiter(10, 5); // bucket=10, leak=5 req/sec

// simulate burst of 15 requests
        for (int i = 1; i <= 15; i++) {
            final int reqId = i;
            boolean accepted = limiter.submitRequest(() -> {
                System.out.println("Processing request " + reqId
                        + " at " + System.currentTimeMillis());
            });

            if (!accepted) {
                System.out.println("Request " + reqId + " dropped");
            }
        }

        Thread.sleep(5000);
        limiter.shutdown();

    }
}