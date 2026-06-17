package com.assignments.RateLimiter;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class SlidingWindowLogRateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final LinkedBlockingDeque<Long> timestamps;

    public SlidingWindowLogRateLimiter(int maxRequests, long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowMillis = TimeUnit.SECONDS.toMillis(windowSeconds);
        this.timestamps = new LinkedBlockingDeque<>(maxRequests * 2); // bounded
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // evict expired timestamps from front
        while (!timestamps.isEmpty() && 
               (now - timestamps.peekFirst()) > windowMillis) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < maxRequests) {
            timestamps.addLast(now);  // log this request
            return true;
        }

        return false; // rate limited
    }

    public static void main(String[] args) throws InterruptedException {
        SlidingWindowLogRateLimiter limiter =
                new SlidingWindowLogRateLimiter(10, 1); // 100 req/sec
        for(int x=0; x<200; x++) {
            if (limiter.allowRequest()) {
                System.out.println("Request allowed "+x);
            } else {
                System.out.println("Request rejected  "+x);
            }
            try {
                Thread.sleep(10);
            }
            catch(Exception ex){}
        }

    }
}