package com.assignments.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

/**
 * Demonstrates all Redis SET options: EX, PX, NX, XX, EXAT, KEEPTTL
 * and the classic NX+EX distributed lock pattern.
 *
 * Requires: Jedis 4.x, Redis running on localhost:6379
 */
public class RedisSetOptionsDemo {

    static final String KEY_PREFIX = "demo";
    static final String RESET  = "\u001B[0m";
    static final String BOLD   = "\u001B[1m";
    static final String CYAN   = "\u001B[36m";
    static final String GREEN  = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String RED    = "\u001B[31m";
    static final String BLUE   = "\u001B[34m";
    static final String DIM    = "\u001B[2m";

    public static void main(String[] args) throws InterruptedException {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            // ═══════════════════════════════════════════════════════
            // STEP 1 — EX : expire in SECONDS
            // Use case: OTP codes, session tokens, rate limit windows
            // ═══════════════════════════════════════════════════════
            header("STEP 1 — EX  :  Set with expiry in SECONDS");

            String keyEx = KEY_PREFIX + ":otp:user123";
            jedis.del(keyEx);

            SetParams exParams = SetParams.setParams().ex(300);
            jedis.set(keyEx, "748291", exParams);

            echo("jedis.set(\"" + keyEx + "\", \"748291\", SetParams.setParams().ex(300))");
            System.out.println();

            row("Key",        keyEx);
            row("Value",      jedis.get(keyEx));
            row("TTL",        jedis.ttl(keyEx) + "s  ← auto-deletes after 300s");
            row("Use case",   "OTP / session token expiry");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 2 — PX : expire in MILLISECONDS
            // Use case: sub-second locks, flash deals, high-freq limits
            // ═══════════════════════════════════════════════════════
            header("STEP 2 — PX  :  Set with expiry in MILLISECONDS");

            String keyPx = KEY_PREFIX + ":flash:deal";
            jedis.del(keyPx);

            SetParams pxParams = SetParams.setParams().px(500);
            jedis.set(keyPx, "ACTIVE", pxParams);

            echo("jedis.set(\"" + keyPx + "\", \"ACTIVE\", SetParams.setParams().px(500))");
            System.out.println();

            row("Key",        keyPx);
            row("Value",      jedis.get(keyPx));
            row("TTL (ms)",   jedis.pttl(keyPx) + "ms  ← ~500ms remaining");
            row("Use case",   "Flash deals, sub-second windows");

            Thread.sleep(600);

            String afterExpiry = jedis.get(keyPx);
            System.out.println();
            rowColored("After 600ms sleep",
                    afterExpiry == null ? "null  →  KEY EXPIRED ✓" : afterExpiry,
                    afterExpiry == null ? GREEN : RED);
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 3 — NX : set ONLY if key does NOT exist
            // Use case: safe init, prevent overwrite, idempotent writes
            // ═══════════════════════════════════════════════════════
            header("STEP 3 — NX  :  Set ONLY if key does NOT exist");

            String keyNx = KEY_PREFIX + ":config:theme";
            jedis.del(keyNx);

            SetParams nxParams = SetParams.setParams().nx();

            echo("jedis.set(\"" + keyNx + "\", \"dark\",  SetParams.setParams().nx())  ← 1st call");
            echo("jedis.set(\"" + keyNx + "\", \"light\", SetParams.setParams().nx())  ← 2nd call");
            System.out.println();

            String r1 = jedis.set(keyNx, "dark",  nxParams);
            String r2 = jedis.set(keyNx, "light", nxParams);

            System.out.printf("  %-22s %-14s %-14s %s%n", "CALL", "ATTEMPT VALUE", "RESULT", "FINAL VALUE");
            divider(70);
            resultRow("1st set (NX)", "dark",  r1, jedis.get(keyNx));
            resultRow("2nd set (NX)", "light", r2, jedis.get(keyNx));
            System.out.println();
            note("2nd call returns null — key already exists, write blocked ✓");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 4 — XX : set ONLY if key ALREADY exists
            // Use case: conditional update, refresh existing record only
            // ═══════════════════════════════════════════════════════
            header("STEP 4 — XX  :  Set ONLY if key ALREADY exists");

            String keyXx = KEY_PREFIX + ":user:status";
            jedis.del(keyXx);

            SetParams xxParams = SetParams.setParams().xx();

            echo("jedis.set(\"" + keyXx + "\", \"online\",  SetParams.setParams().xx())  ← key absent");
            echo("jedis.set(\"" + keyXx + "\", \"offline\")                              ← seed key");
            echo("jedis.set(\"" + keyXx + "\", \"online\",  SetParams.setParams().xx())  ← key present");
            System.out.println();

            String xx1 = jedis.set(keyXx, "online",  xxParams);   // key absent → fail
            jedis.set(keyXx, "offline");                           // seed
            String xx2 = jedis.set(keyXx, "online",  xxParams);   // key present → succeed

            System.out.printf("  %-28s %-14s %-14s %s%n", "CALL", "VALUE", "RESULT", "STORED VALUE");
            divider(70);
            resultRow("set(XX) — key absent",  "online",  xx1, "n/a");
            resultRow("set(plain) seed",        "offline", "OK", "offline");
            resultRow("set(XX) — key present", "online",  xx2, jedis.get(keyXx));
            System.out.println();
            note("XX is the inverse of NX — only updates, never creates ✓");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 5 — EXAT : expire at absolute Unix timestamp
            // Use case: expire at midnight, end-of-sale, fixed wall time
            // ═══════════════════════════════════════════════════════
            header("STEP 5 — EXAT  :  Set expiry as absolute Unix timestamp");

            String keyExAt = KEY_PREFIX + ":promo:diwali";
            jedis.del(keyExAt);

            long expiresAt = (System.currentTimeMillis() / 1000) + 10;
            SetParams exAtParams = SetParams.setParams().exAt(expiresAt);
            jedis.set(keyExAt, "20% OFF", exAtParams);

            echo("jedis.set(\"" + keyExAt + "\", \"20% OFF\", SetParams.setParams().exAt(" + expiresAt + "))");
            System.out.println();

            row("Key",           keyExAt);
            row("Value",         jedis.get(keyExAt));
            row("Expires at",    expiresAt + "  (Unix timestamp)");
            row("TTL remaining", jedis.ttl(keyExAt) + "s");
            row("Use case",      "Promo / sale that ends at a fixed wall-clock time");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 6 — KEEPTTL : update value, preserve existing TTL
            // Use case: refresh session payload without extending expiry
            // ═══════════════════════════════════════════════════════
            header("STEP 6 — KEEPTTL  :  Update value WITHOUT resetting TTL");

            String keyKttl = KEY_PREFIX + ":session:abc";
            jedis.del(keyKttl);

            jedis.set(keyKttl, "user_data_v1", SetParams.setParams().ex(30));
            long ttlBefore = jedis.ttl(keyKttl);

            Thread.sleep(2000);  // let 2s elapse

            jedis.set(keyKttl, "user_data_v2", SetParams.setParams().keepTtl());
            long ttlAfter = jedis.ttl(keyKttl);

            echo("jedis.set(\"" + keyKttl + "\", \"user_data_v1\", SetParams.setParams().ex(30))");
            echo("Thread.sleep(2000)  ← 2 seconds pass");
            echo("jedis.set(\"" + keyKttl + "\", \"user_data_v2\", SetParams.setParams().keepTtl())");
            System.out.println();

            System.out.printf("  %-28s %-20s %-20s%n", "STATE", "VALUE", "TTL");
            divider(70);
            System.out.printf("  %-28s " + CYAN + "%-20s" + RESET + GREEN + "%-20s" + RESET + "%n",
                    "After initial set",   "user_data_v1", ttlBefore + "s");
            System.out.printf("  %-28s " + CYAN + "%-20s" + RESET + YELLOW + "%-20s" + RESET + "%n",
                    "After KEEPTTL update", "user_data_v2", ttlAfter + "s  ← NOT reset to 30");
            System.out.println();
            note("Without KEEPTTL, a plain SET would reset TTL to -1 (no expiry) ✓");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 7 — NX + EX : Distributed Lock pattern
            // Use case: ensure exactly one worker processes a payment
            // ═══════════════════════════════════════════════════════
            header("STEP 7 — NX + EX  :  Distributed Lock pattern");

            String lockKey = KEY_PREFIX + ":lock:payment:txn_9981";
            jedis.del(lockKey);

            SetParams lockParams = SetParams.setParams().nx().ex(10);

            echo("jedis.set(lockKey, \"worker-1\", SetParams.setParams().nx().ex(10))  ← Worker 1");
            echo("jedis.set(lockKey, \"worker-2\", SetParams.setParams().nx().ex(10))  ← Worker 2");
            System.out.println();

            String w1 = jedis.set(lockKey, "worker-1", lockParams);
            String w2 = jedis.set(lockKey, "worker-2", lockParams);

            System.out.printf("  %-20s %-14s %-14s %s%n", "WORKER", "ATTEMPT", "RESULT", "LOCK OWNER");
            divider(65);

            String owner1 = jedis.get(lockKey);
            System.out.printf("  %-20s %-14s " + (w1 != null ? GREEN : RED) + "%-14s" + RESET + " %s%n",
                    "Worker-1", "acquire", w1 != null ? "ACQUIRED ✓" : "FAILED ✗", owner1);
            System.out.printf("  %-20s %-14s " + (w2 != null ? GREEN : RED) + "%-14s" + RESET + " %s%n",
                    "Worker-2", "acquire", w2 != null ? "ACQUIRED ✓" : "FAILED ✗", owner1);

            // Safe release — only delete if we still own it
            String currentOwner = jedis.get(lockKey);
            if ("worker-1".equals(currentOwner)) {
                jedis.del(lockKey);
            }
            String w3 = jedis.set(lockKey, "worker-2", lockParams);
            System.out.printf("  %-20s %-14s " + (w3 != null ? GREEN : RED) + "%-14s" + RESET + " %s%n",
                    "Worker-1 releases", "del(lockKey)", "", "");
            System.out.printf("  %-20s %-14s " + (w3 != null ? GREEN : RED) + "%-14s" + RESET + " %s%n",
                    "Worker-2", "retry", w3 != null ? "ACQUIRED ✓" : "FAILED ✗", jedis.get(lockKey));

            System.out.println();
            note("EX ensures lock auto-expires if holder crashes — prevents deadlock ✓");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // FINAL SUMMARY
            // ═══════════════════════════════════════════════════════
            header("FINAL — Redis SET Options Quick Reference");

            System.out.printf("  " + BOLD + "%-10s %-12s %-38s %s" + RESET + "%n",
                    "OPTION", "UNIT", "MEANING", "TYPICAL USE CASE");
            divider(80);
            summaryRow("EX",      "seconds",  "TTL in seconds",                  "OTP, session, rate window");
            summaryRow("PX",      "ms",       "TTL in milliseconds",             "Flash deals, sub-second locks");
            summaryRow("NX",      "-",        "Set only if key absent",          "Safe init, idempotent write");
            summaryRow("XX",      "-",        "Set only if key present",         "Conditional update");
            summaryRow("EXAT",    "unix ts",  "Expire at absolute timestamp",    "End-of-sale, midnight expiry");
            summaryRow("KEEPTTL", "-",        "Preserve existing TTL on update", "Session payload refresh");
            summaryRow("NX+EX",   "combo",    "Acquire lock, auto-release",      "Distributed lock / mutex");
            System.out.println();

            // cleanup
            jedis.del(keyEx, keyNx, keyXx, keyExAt, keyKttl, lockKey);
            System.out.println(DIM + "  cleanup done — all demo keys deleted" + RESET);
            System.out.println();
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    static void header(String title) {
        System.out.println();
        System.out.println(BOLD + BLUE +
                "╔══════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + BLUE + "║  " + RESET + BOLD + title + RESET);
        System.out.println(BOLD + BLUE +
                "╚══════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    static void divider(int len) {
        System.out.println(DIM + "  " + "─".repeat(len) + RESET);
    }

    static void echo(String cmd) {
        System.out.println(DIM + "  " + cmd + RESET);
    }

    static void note(String msg) {
        System.out.println("  " + YELLOW + "⚠  " + msg + RESET);
    }

    static void row(String label, String value) {
        System.out.printf("  " + CYAN + "%-18s" + RESET + " %s%n", label, value);
    }

    static void rowColored(String label, String value, String color) {
        System.out.printf("  " + CYAN + "%-18s" + RESET + color + " %s" + RESET + "%n", label, value);
    }

    static void resultRow(String call, String attempt, String result, String stored) {
        String color = (result != null && result.equals("OK")) ? GREEN : RED;
        System.out.printf("  %-28s %-14s " + color + "%-14s" + RESET + " %s%n",
                call, attempt, result == null ? "null (blocked)" : result, stored);
    }

    static void summaryRow(String opt, String unit, String meaning, String useCase) {
        System.out.printf("  " + CYAN + "%-10s" + RESET +
                YELLOW + "%-12s" + RESET +
                "%-38s " + DIM + "%s" + RESET + "%n",
                opt, unit, meaning, useCase);
    }
}