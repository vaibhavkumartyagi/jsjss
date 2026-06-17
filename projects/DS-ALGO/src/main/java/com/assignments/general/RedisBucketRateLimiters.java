package com.assignments.general;

import redis.clients.jedis.Jedis;

/**
 * Demonstrates Token Bucket and Leaky Bucket rate limiting algorithms
 * both implemented using Redis primitives.
 *
 * TOKEN BUCKET  — burst-friendly, refills tokens over time
 * LEAKY BUCKET  — strict steady output rate, queues excess requests
 *
 * Requires: Jedis 4.x, Redis on localhost:6379
 */
public class RedisBucketRateLimiters {

    // ── ANSI ─────────────────────────────────────────────────────────────────
    static final String RESET  = "\u001B[0m";
    static final String BOLD   = "\u001B[1m";
    static final String CYAN   = "\u001B[36m";
    static final String GREEN  = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String RED    = "\u001B[31m";
    static final String BLUE   = "\u001B[34m";
    static final String DIM    = "\u001B[2m";
    static final String MAGENTA= "\u001B[35m";

    // ── Token Bucket config ───────────────────────────────────────────────────
    static final int    TB_CAPACITY        = 10;   // max tokens in bucket
    static final int    TB_REFILL_RATE     = 2;    // tokens added per second
    static final String TB_KEY_TOKENS      = "tb:tokens";
    static final String TB_KEY_LAST_REFILL = "tb:last_refill";

    // ── Leaky Bucket config ───────────────────────────────────────────────────
    static final int    LB_CAPACITY        = 10;   // max queue depth
    static final int    LB_LEAK_RATE       = 2;    // requests leaked (processed) per second
    static final String LB_KEY_QUEUE       = "lb:queue";
    static final String LB_KEY_LAST_LEAK   = "lb:last_leak";

    // ── Lua: Token Bucket (atomic refill + consume) ───────────────────────────
    static final String LUA_TOKEN_BUCKET = """
        local tokens_key      = KEYS[1]
        local last_refill_key = KEYS[2]
        local capacity        = tonumber(ARGV[1])
        local refill_rate     = tonumber(ARGV[2])
        local now             = tonumber(ARGV[3])

        local tokens      = tonumber(redis.call('GET', tokens_key))      or capacity
        local last_refill = tonumber(redis.call('GET', last_refill_key)) or now

        -- calculate how many tokens to add since last refill
        local elapsed       = math.max(0, now - last_refill)
        local refill_amount = elapsed * refill_rate
        tokens = math.min(capacity, tokens + refill_amount)

        -- try to consume one token
        if tokens >= 1 then
            tokens = tokens - 1
            redis.call('SET', tokens_key,      tokens)
            redis.call('SET', last_refill_key, now)
            return {1, tokens}   -- allowed, remaining tokens
        else
            redis.call('SET', last_refill_key, now)
            return {0, tokens}   -- denied, 0 tokens left
        end
        """;

    // ── Lua: Leaky Bucket (atomic leak + enqueue) ─────────────────────────────
    static final String LUA_LEAKY_BUCKET = """
        local queue_key     = KEYS[1]
        local last_leak_key = KEYS[2]
        local capacity      = tonumber(ARGV[1])
        local leak_rate     = tonumber(ARGV[2])
        local now           = tonumber(ARGV[3])

        local queue_size = tonumber(redis.call('GET', queue_key))    or 0
        local last_leak  = tonumber(redis.call('GET', last_leak_key)) or now

        -- leak (drain) requests that have been processed since last check
        local elapsed    = math.max(0, now - last_leak)
        local leaked     = math.floor(elapsed * leak_rate)
        queue_size = math.max(0, queue_size - leaked)

        -- try to enqueue the new request
        if queue_size < capacity then
            queue_size = queue_size + 1
            redis.call('SET', queue_key,     queue_size)
            redis.call('SET', last_leak_key, now)
            return {1, queue_size}   -- allowed, current queue depth
        else
            redis.call('SET', last_leak_key, now)
            return {0, queue_size}   -- denied, queue full
        end
        """;

    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            cleanup(jedis);

            // ═══════════════════════════════════════════════════════
            // SECTION 1 — TOKEN BUCKET : concept
            // ═══════════════════════════════════════════════════════
            header("TOKEN BUCKET  —  Concept");

            System.out.println("  " + CYAN + "How it works:" + RESET);
            System.out.println("  • Bucket starts full with " + YELLOW + TB_CAPACITY + " tokens" + RESET);
            System.out.println("  • Each request consumes " + YELLOW + "1 token" + RESET);
            System.out.println("  • Tokens refill at " + YELLOW + TB_REFILL_RATE + " per second" + RESET);
            System.out.println("  • Burst allowed up to " + YELLOW + "capacity" + RESET + " (all tokens at once)");
            System.out.println("  • Request rejected when " + RED + "tokens = 0" + RESET);
            System.out.println();
            System.out.println("  " + DIM + "Redis keys:" + RESET);
            echo("tb:tokens      → current token count  (String / float)");
            echo("tb:last_refill → epoch-second of last refill (String)");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // SECTION 2 — TOKEN BUCKET : burst demo
            // ═══════════════════════════════════════════════════════
            header("TOKEN BUCKET  —  DEMO 1 : Burst of 12 requests (capacity=10)");
            echo("Lua: GET tokens → refill by elapsed*rate → consume 1 → SET tokens");
            System.out.println();

            System.out.printf("  %-6s %-12s %-12s %-10s %s%n",
                    "REQ#", "STATUS", "TOKENS LEFT", "DECISION", "VISUAL");
            divider(65);

            for (int i = 1; i <= 12; i++) {
                long now = System.currentTimeMillis() / 1000;
                var result = tokenBucketRequest(jedis, "user_A", now);
                boolean allowed = result[0] == 1;
                int remaining   = (int) result[1];
                printBucketRow(i, allowed, remaining, TB_CAPACITY, "tokens");
            }
            System.out.println();
            note("Requests 11 & 12 rejected — bucket drained after 10 burst requests");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // SECTION 3 — TOKEN BUCKET : refill demo
            // ═══════════════════════════════════════════════════════
            header("TOKEN BUCKET  —  DEMO 2 : Refill after 2s sleep");
            echo("After 2s sleep: refill_amount = 2s * " + TB_REFILL_RATE + " rate = +" + (2 * TB_REFILL_RATE) + " tokens");
            System.out.println();

            System.out.println("  " + DIM + "Sleeping 2 seconds..." + RESET);
            Thread.sleep(2000);
            System.out.println();

            System.out.printf("  %-6s %-12s %-12s %-10s %s%n",
                    "REQ#", "STATUS", "TOKENS LEFT", "DECISION", "VISUAL");
            divider(65);

            for (int i = 1; i <= 5; i++) {
                long now = System.currentTimeMillis() / 1000;
                var result = tokenBucketRequest(jedis, "user_A", now);
                boolean allowed = result[0] == 1;
                int remaining   = (int) result[1];
                printBucketRow(i, allowed, remaining, TB_CAPACITY, "tokens");
            }
            System.out.println();
            note("First 4 requests pass — bucket refilled 4 tokens (2s * rate=2). 5th rejected.");
            System.out.println();

            cleanup(jedis);

            // ═══════════════════════════════════════════════════════
            // SECTION 4 — LEAKY BUCKET : concept
            // ═══════════════════════════════════════════════════════
            header("LEAKY BUCKET  —  Concept");

            System.out.println("  " + MAGENTA + "How it works:" + RESET);
            System.out.println("  • Bucket is a " + YELLOW + "queue of depth " + LB_CAPACITY + RESET);
            System.out.println("  • Each request " + YELLOW + "enqueues" + RESET + " into the bucket");
            System.out.println("  • Bucket leaks (processes) at " + YELLOW + LB_LEAK_RATE + " req/sec" + RESET + " — always steady");
            System.out.println("  • Burst NOT allowed — excess requests " + RED + "rejected" + RESET + " if queue full");
            System.out.println("  • Guarantees " + GREEN + "smooth, constant output rate" + RESET);
            System.out.println();
            System.out.println("  " + DIM + "Redis keys:" + RESET);
            echo("lb:queue      → current queue depth  (String / int)");
            echo("lb:last_leak  → epoch-second of last leak drain (String)");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // SECTION 5 — LEAKY BUCKET : burst rejection demo
            // ═══════════════════════════════════════════════════════
            header("LEAKY BUCKET  —  DEMO 1 : Burst of 12 requests (capacity=10)");
            echo("Lua: GET queue → drain by floor(elapsed*rate) → enqueue 1 → SET queue");
            System.out.println();

            System.out.printf("  %-6s %-12s %-12s %-10s %s%n",
                    "REQ#", "STATUS", "QUEUE DEPTH", "DECISION", "VISUAL");
            divider(65);

            for (int i = 1; i <= 12; i++) {
                long now = System.currentTimeMillis() / 1000;
                var result = leakyBucketRequest(jedis, "user_B", now);
                boolean allowed = result[0] == 1;
                int queueDepth  = (int) result[1];
                printBucketRow(i, allowed, queueDepth, LB_CAPACITY, "queued");
            }
            System.out.println();
            note("Requests 11 & 12 rejected — queue full at depth 10, no leaking yet");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // SECTION 6 — LEAKY BUCKET : drain demo
            // ═══════════════════════════════════════════════════════
            header("LEAKY BUCKET  —  DEMO 2 : Queue drains after 3s sleep");
            echo("After 3s sleep: leaked = floor(3s * " + LB_LEAK_RATE + " rate) = " + (3 * LB_LEAK_RATE) + " slots freed");
            System.out.println();

            System.out.println("  " + DIM + "Sleeping 3 seconds..." + RESET);
            Thread.sleep(3000);
            System.out.println();

            System.out.printf("  %-6s %-12s %-12s %-10s %s%n",
                    "REQ#", "STATUS", "QUEUE DEPTH", "DECISION", "VISUAL");
            divider(65);

            for (int i = 1; i <= 8; i++) {
                long now = System.currentTimeMillis() / 1000;
                var result = leakyBucketRequest(jedis, "user_B", now);
                boolean allowed = result[0] == 1;
                int queueDepth  = (int) result[1];
                printBucketRow(i, allowed, queueDepth, LB_CAPACITY, "queued");
            }
            System.out.println();
            note("6 slots freed (3s * rate=2). First 6 pass, 7th & 8th rejected — queue full again.");
            System.out.println();

            cleanup(jedis);

            // ═══════════════════════════════════════════════════════
            // FINAL COMPARISON
            // ═══════════════════════════════════════════════════════
            header("FINAL  —  Token Bucket vs Leaky Bucket Comparison");

            System.out.printf("  " + BOLD + "%-22s %-30s %-30s" + RESET + "%n",
                    "DIMENSION", "TOKEN BUCKET", "LEAKY BUCKET");
            divider(82);
            compRow("Burst allowed?",     GREEN  + "Yes — up to capacity" + RESET,
                                          RED    + "No  — strict queue"   + RESET);
            compRow("Output rate",        YELLOW + "Variable (bursty)"    + RESET,
                                          GREEN  + "Constant (smooth)"    + RESET);
            compRow("Excess handling",    RED    + "Request rejected"      + RESET,
                                          RED    + "Request rejected"      + RESET);
            compRow("Redis state",        CYAN   + "tokens + last_refill"  + RESET,
                                          CYAN   + "queue  + last_leak"   + RESET);
            compRow("Atomicity",          "Lua script (INCR+SET)",
                                          "Lua script (drain+SET)");
            compRow("Best for",           "APIs allowing short bursts",
                                          "Payments, strict SLA systems");
            compRow("Example",            "GitHub API, Google Maps API",
                                          "Payment gateway, SMS sender");
            System.out.println();

            System.out.println(DIM + "  cleanup done — all demo keys deleted" + RESET);
            System.out.println();
        }
    }

    // ── Token Bucket : execute Lua ────────────────────────────────────────────
    static long[] tokenBucketRequest(Jedis jedis, String userId, long nowSec) {
        String tokensKey     = "tb:" + userId + ":tokens";
        String lastRefillKey = "tb:" + userId + ":last_refill";

        @SuppressWarnings("unchecked")
        var res = (java.util.List<Long>) jedis.eval(
                LUA_TOKEN_BUCKET, 2,
                tokensKey, lastRefillKey,
                String.valueOf(TB_CAPACITY),
                String.valueOf(TB_REFILL_RATE),
                String.valueOf(nowSec)
        );
        return new long[]{ res.get(0), res.get(1) };
    }

    // ── Leaky Bucket : execute Lua ────────────────────────────────────────────
    static long[] leakyBucketRequest(Jedis jedis, String userId, long nowSec) {
        String queueKey    = "lb:" + userId + ":queue";
        String lastLeakKey = "lb:" + userId + ":last_leak";

        @SuppressWarnings("unchecked")
        var res = (java.util.List<Long>) jedis.eval(
                LUA_LEAKY_BUCKET, 2,
                queueKey, lastLeakKey,
                String.valueOf(LB_CAPACITY),
                String.valueOf(LB_LEAK_RATE),
                String.valueOf(nowSec)
        );
        return new long[]{ res.get(0), res.get(1) };
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    static void header(String title) {
        System.out.println();
        System.out.println(BOLD + BLUE +
                "╔══════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + BLUE + "║  " + RESET + BOLD + title + RESET);
        System.out.println(BOLD + BLUE +
                "╚══════════════════════════════════════════════════════════════╝" + RESET);
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

    static void printBucketRow(int req, boolean allowed, int level, int capacity, String unit) {
        String status   = allowed ? GREEN + "ALLOWED" + RESET : RED + "REJECTED" + RESET;
        String decision = allowed ? GREEN + "✓ pass"  + RESET : RED + "✗ drop"   + RESET;
        String bar      = bucketBar(level, capacity, 20);
        System.out.printf("  %-6s %-20s %-6s %-18s %s%n",
                "#" + req, status, level + " " + unit, decision, bar);
    }

    static String bucketBar(int level, int capacity, int width) {
        int filled = capacity > 0 ? (int) ((level / (double) capacity) * width) : 0;
        String color = filled > (width * 8 / 10) ? RED
                     : filled > (width * 4 / 10) ? YELLOW
                     : GREEN;
        return color  + "█".repeat(Math.max(0, filled))  + RESET +
               DIM    + "░".repeat(Math.max(0, width - filled)) + RESET;
    }

    static void compRow(String dimension, String tokenBucket, String leakyBucket) {
        System.out.printf("  %-22s %-42s %s%n", dimension, tokenBucket, leakyBucket);
    }

    static void cleanup(Jedis jedis) {
        jedis.del(TB_KEY_TOKENS, TB_KEY_LAST_REFILL, LB_KEY_QUEUE, LB_KEY_LAST_LEAK);
        // also clean per-user keys from demos
        jedis.del("tb:user_A:tokens", "tb:user_A:last_refill");
        jedis.del("lb:user_B:queue",  "lb:user_B:last_leak");
    }
}