package com.RateLimiter;

import java.util.concurrent.TimeUnit;

public class TokenBucket {

    private double rateOfFill;
    private double tokens;
    private int capacity;
    private long lastWindowRefillTime;

    public TokenBucket() {
        rateOfFill = 3;
        capacity = 7;
        tokens = 7;
        lastWindowRefillTime = System.currentTimeMillis();
    }
    // need to refill bucket till max capacity every time new req come
    // how many token need to refill = (now() - then() + total token filled at rate
    private void refill() {
        long now = System.currentTimeMillis();
        double timeElapsedInSec = (double) (now - lastWindowRefillTime) / 1000; // time calculate
        tokens = Math.min(capacity, tokens + (timeElapsedInSec * rateOfFill)); // refill token + refill
        lastWindowRefillTime = now;
    }

    private boolean isAllowed() {
        refill();
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    public void run(TokenBucket tb, int sleepMs) {
        sleep(sleepMs);

        System.out.println("##########################\n");
        for (int x = 0; x < 1000; x++) {
            System.out.println(currentTime() + " req no " + x + " is_allowed " + tb.isAllowed());
            sleep(100);
        }

        sleep(sleepMs);

        System.out.println("##########################\n");
        for (int x = 10; x < 20; x++) {
            System.out.println(currentTime() + " req no " + x + " is_allowed " + tb.isAllowed());
        }
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private String currentTime() {
        long ms = System.currentTimeMillis();
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) % 60,
                TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        );
    }

    public static void main(String[] args) {
        TokenBucket tb = new TokenBucket();
        tb.run(tb, 1000);
        tb.run(tb, 1000);
        tb.run(tb, 2000);
        tb.run(tb, 1000);
    }
}