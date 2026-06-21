package com.assignments.redis;

import redis.clients.jedis.Jedis;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════
 *  REDIS LUA SCRIPT TUTORIAL — Learn Lua through Java + Redis
 * ═══════════════════════════════════════════════════════════════════
 *
 *  Each section teaches ONE Lua concept and shows:
 *    1. The Lua script (as a Java String)
 *    2. The jedis.eval() call
 *    3. The output — so you see exactly what Lua returns to Java
 *
 *  Requires: Jedis 4.x, Redis on localhost:6379
 */
public class RedisLuaScriptTutorial {

    // ── ANSI ─────────────────────────────────────────────────────────────────
    static final String RESET   = "\u001B[0m";
    static final String BOLD    = "\u001B[1m";
    static final String CYAN    = "\u001B[36m";
    static final String GREEN   = "\u001B[32m";
    static final String YELLOW  = "\u001B[33m";
    static final String RED     = "\u001B[31m";
    static final String BLUE    = "\u001B[34m";
    static final String MAGENTA = "\u001B[35m";
    static final String DIM     = "\u001B[2m";

    public static void main(String[] args) throws InterruptedException {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            jedis.flushDB(); // clean slate

            // ═══════════════════════════════════════════════════════
            // LESSON 1 — Hello World: return a plain value from Lua
            // ═══════════════════════════════════════════════════════
            header("LESSON 1  —  Hello World  :  return a plain value");

            concept(
                "Lua can return strings, numbers, or tables.",
                "return 'hello'  sends a String back to Java.",
                "'return' in Lua = what jedis.eval() gives you."
            );

            String lua1 = "return 'Hello from Lua!'";

            luaBlock(lua1);
            javaCall("jedis.eval(lua1, 0)   ← 0 = no KEYS");

            Object result1 = jedis.eval(lua1, 0);
            output("Java received", result1);
            typeOf("result1", result1);
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 2 — KEYS and ARGV: passing data into Lua
            // ═══════════════════════════════════════════════════════
            header("LESSON 2  —  KEYS and ARGV  :  passing data into Lua");

            concept(
                "KEYS[] = Redis key names you pass in.",
                "ARGV[] = extra arguments (numbers, strings).",
                "Both are 1-indexed in Lua  (KEYS[1] not KEYS[0])."
            );

            String lua2 = """
                local key  = KEYS[1]
                local value = ARGV[1]
                local ttl   = ARGV[2]
                return 'key=' .. key .. '  value=' .. value .. '  ttl=' .. ttl
                """;

            luaBlock(lua2);
            javaCall("jedis.eval(lua2, 1, \"mykey\", \"sigma\", \"60\")");
            javaCall("              ↑numKeys  ↑KEYS[1]  ↑ARGV[1]  ↑ARGV[2]");

            Object result2 = jedis.eval(lua2, 1, "mykey", "sigma", "60");
            output("Java received", result2);
            System.out.println();
            warn("Everything arrives as String in Lua — always use tonumber() for math!");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 3 — tonumber(): string → number conversion
            // ═══════════════════════════════════════════════════════
            header("LESSON 3  —  tonumber()  :  convert String args to numbers");

            concept(
                "ARGV values are always Strings — even if you pass 100.",
                "tonumber('100') → 100  (number type in Lua).",
                "Without tonumber(), math operations break or give nil."
            );

            String lua3 = """
                local a = ARGV[1]              -- still a String '10'
                local b = ARGV[2]              -- still a String '3'
                local wrong  = a + b           -- Lua auto-converts here, but risky
                local correct = tonumber(a) + tonumber(b)
                local floored = math.floor(tonumber(a) / tonumber(b))
                local maxed   = math.max(tonumber(a), tonumber(b))
                local mined   = math.min(tonumber(a), tonumber(b))
                return correct .. ' floor=' .. floored .. ' max=' .. maxed .. ' min=' .. mined
                """;

            luaBlock(lua3);
            javaCall("jedis.eval(lua3, 0, \"10\", \"3\")");

            Object result3 = jedis.eval(lua3, 0, "10", "3");
            output("Java received", result3);
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 4 — redis.call(): talking to Redis from Lua
            // ═══════════════════════════════════════════════════════
            header("LESSON 4  —  redis.call()  :  run Redis commands inside Lua");

            concept(
                "redis.call('COMMAND', args...)  executes any Redis command.",
                "Return value of redis.call = what that command normally returns.",
                "GET returns String or nil.  INCR returns Long.  SET returns 'OK'."
            );

            String lua4 = """
                redis.call('SET', KEYS[1], ARGV[1])
                local val = redis.call('GET', KEYS[1])
                redis.call('EXPIRE', KEYS[1], 60)
                local ttl = redis.call('TTL', KEYS[1])
                return 'stored=' .. val .. '  ttl=' .. ttl .. 's'
                """;

            luaBlock(lua4);
            javaCall("jedis.eval(lua4, 1, \"lua:demo\", \"hello-redis\")");

            Object result4 = jedis.eval(lua4, 1, "lua:demo", "hello-redis");
            output("Java received", result4);
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 5 — nil and 'or' as null-coalescing
            // ═══════════════════════════════════════════════════════
            header("LESSON 5  —  nil and 'or'  :  handling missing keys safely");

            concept(
                "GET on a missing key returns nil  (Lua's null).",
                "tonumber(nil) = nil — causes errors in math.",
                "Use 'or' as null-coalescing:  tonumber(val) or 0"
            );

            jedis.del("missing:key", "present:key");
            jedis.set("present:key", "42");

            String lua5 = """
                local missing_val = redis.call('GET', KEYS[1])
                local present_val = redis.call('GET', KEYS[2])

                -- 'or' gives fallback when value is nil
                local safe_missing = tonumber(missing_val) or 0
                local safe_present = tonumber(present_val) or 0

                return 'missing=' .. safe_missing .. '  present=' .. safe_present
                """;

            luaBlock(lua5);
            javaCall("jedis.eval(lua5, 2, \"missing:key\", \"present:key\")");

            Object result5 = jedis.eval(lua5, 2, "missing:key", "present:key");
            output("Java received", result5);
            System.out.println();
            warn("'or 0' is the Lua equivalent of Java's  != null ? val : 0");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 6 — if / else / end
            // ═══════════════════════════════════════════════════════
            header("LESSON 6  —  if / else / end  :  conditional logic");

            concept(
                "Lua if-else syntax:  if <cond> then ... else ... end",
                "No braces {} — blocks end with 'end' keyword.",
                "return stops the script immediately, like Java."
            );

            String lua6 = """
                local count    = tonumber(redis.call('INCR', KEYS[1]))
                local limit    = tonumber(ARGV[1])

                if count == 1 then
                    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))
                end

                if count > limit then
                    return {0, count, 'DENIED'}
                else
                    return {1, count, 'ALLOWED'}
                end
                """;

            luaBlock(lua6);
            System.out.println();
            System.out.printf("  %-6s %-14s %-14s %s%n", "REQ#", "STATUS", "COUNT", "LIMIT");
            divider(50);

            jedis.del("lesson6:counter");
            for (int i = 1; i <= 7; i++) {
                javaCall("jedis.eval(lua6, 1, \"lesson6:counter\", \"5\", \"60\")  ← req #" + i);
                @SuppressWarnings("unchecked")
                List<Object> res6 = (List<Object>) jedis.eval(lua6, 1,
                        "lesson6:counter", "5", "60");
                long allowed = (Long) res6.get(0);
                long count   = (Long) res6.get(1);
                String label = (String) res6.get(2);
                String color = allowed == 1 ? GREEN : RED;
                System.out.printf("  %-6s " + color + "%-14s" + RESET + "%-14s %s%n",
                        "#" + i, label, "count=" + count, "limit=5");
            }
            System.out.println();
            warn("count==1 check → EXPIRE only on first request (avoids race condition)");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 7 — Tables: returning multiple values to Java
            // ═══════════════════════════════════════════════════════
            header("LESSON 7  —  Tables { }  :  return multiple values to Java");

            concept(
                "Lua tables = array + map in one structure.",
                "return {1, 42, 'ok'}  → Java gets List<Object>.",
                "Access with res.get(0), res.get(1), res.get(2) in Java."
            );

            String lua7 = """
                local a = tonumber(ARGV[1])
                local b = tonumber(ARGV[2])
                return {
                    a + b,
                    a - b,
                    a * b,
                    math.floor(a / b),
                    a .. ' and ' .. b
                }
                """;

            luaBlock(lua7);
            javaCall("jedis.eval(lua7, 0, \"10\", \"3\")");
            javaCall("→ returns List<Object>  with 5 elements");

            @SuppressWarnings("unchecked")
            List<Object> result7 = (List<Object>) jedis.eval(lua7, 0, "10", "3");

            System.out.println();
            System.out.printf("  %-14s %-14s %s%n", "Java index", "Value", "Operation");
            divider(45);
            String[] ops7 = {"10 + 3", "10 - 3", "10 * 3", "floor(10/3)", "concat"};
            for (int i = 0; i < result7.size(); i++) {
                System.out.printf("  " + CYAN + "res.get(%d)" + RESET + "     " +
                        YELLOW + "%-14s" + RESET + " %s%n",
                        i, result7.get(i), ops7[i]);
            }
            System.out.println();
            warn("Table values map to Java types: Lua number→Long, Lua string→String");
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 8 — local variables and scope
            // ═══════════════════════════════════════════════════════
            header("LESSON 8  —  local  :  always declare variables as local");

            concept(
                "Without 'local', variables are GLOBAL in Lua.",
                "Global vars leak between scripts — can corrupt state.",
                "Always prefix every variable with 'local'."
            );

            String lua8_bad = """
                count = 0          -- BAD: global variable!
                count = count + 1
                return count
                """;

            String lua8_good = """
                local count = 0    -- GOOD: local variable
                local limit = tonumber(ARGV[1])
                local key   = KEYS[1]
                local val   = tonumber(redis.call('GET', key)) or 0
                val = val + 1
                redis.call('SET', key, val)
                return {val, limit, val <= limit and 'ok' or 'over'}
                """;

            System.out.println("  " + RED + "✗  BAD — global variable (leaks between scripts):" + RESET);
            luaBlock(lua8_bad);
            System.out.println("  " + GREEN + "✓  GOOD — local variable (safe, scoped):" + RESET);
            luaBlock(lua8_good);
            javaCall("jedis.eval(lua8_good, 1, \"lesson8:val\", \"3\")");

            jedis.del("lesson8:val");
            @SuppressWarnings("unchecked")
            List<Object> result8 = (List<Object>) jedis.eval(lua8_good, 1, "lesson8:val", "3");
            System.out.printf("  " + CYAN + "res.get(0)" + RESET + " = %s  (current value)%n",  result8.get(0));
            System.out.printf("  " + CYAN + "res.get(1)" + RESET + " = %s  (limit)%n",          result8.get(1));
            System.out.printf("  " + CYAN + "res.get(2)" + RESET + " = %s  (status)%n",         result8.get(2));
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // LESSON 9 — Full Token Bucket: all concepts combined
            // ═══════════════════════════════════════════════════════
            header("LESSON 9  —  Full Token Bucket  :  all concepts together");

            concept(
                "KEYS[1,2] → key names passed from Java.",
                "ARGV[1,2,3] → capacity, refill_rate, now (unix seconds).",
                "tonumber + or + math.min + if/else + table return — all combined."
            );

            String luaTokenBucket = """
                -- KEYS[1] = tokens key
                -- KEYS[2] = last_refill key
                -- ARGV[1] = capacity   (max tokens)
                -- ARGV[2] = refill_rate (tokens per second)
                -- ARGV[3] = now         (current unix second)

                local capacity     = tonumber(ARGV[1])
                local refill_rate  = tonumber(ARGV[2])
                local now          = tonumber(ARGV[3])

                -- GET current state; fallback if key missing
                local tokens      = tonumber(redis.call('GET', KEYS[1])) or capacity
                local last_refill = tonumber(redis.call('GET', KEYS[2])) or now

                -- refill: add tokens proportional to elapsed time
                local elapsed      = math.max(0, now - last_refill)
                local refill_amt   = elapsed * refill_rate
                tokens = math.min(capacity, tokens + refill_amt)

                -- consume 1 token if available
                if tokens >= 1 then
                    tokens = tokens - 1
                    redis.call('SET', KEYS[1], tokens)
                    redis.call('SET', KEYS[2], now)
                    return {1, tokens, 'ALLOWED'}
                else
                    redis.call('SET', KEYS[2], now)
                    return {0, 0, 'DENIED'}
                end
                """;

            luaBlock(luaTokenBucket);

            System.out.println();
            System.out.printf("  %-6s %-12s %-14s %s%n", "REQ#", "DECISION", "TOKENS LEFT", "VISUAL");
            divider(55);

            jedis.del("tb:lesson9:tokens", "tb:lesson9:last_refill");
            for (int i = 1; i <= 8; i++) {
                long now = System.currentTimeMillis() / 1000;
                @SuppressWarnings("unchecked")
                List<Object> res = (List<Object>) jedis.eval(
                        luaTokenBucket, 2,
                        "tb:lesson9:tokens", "tb:lesson9:last_refill",
                        "5",                 // capacity
                        "1",                 // refill_rate
                        String.valueOf(now)
                );
                long allowed  = (Long) res.get(0);
                long tokens   = (Long) res.get(1);
                String status = (String) res.get(2);
                String color  = allowed == 1 ? GREEN : RED;
                String bar    = bar((int) tokens, 5, 16);
                System.out.printf("  %-6s " + color + "%-12s" + RESET + "%-14s %s%n",
                        "#" + i, status, tokens + " tokens", bar);
            }
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // FINAL CHEATSHEET
            // ═══════════════════════════════════════════════════════
            header("FINAL  —  Lua-for-Redis Cheatsheet");

            System.out.printf("  " + BOLD + "%-28s  %-30s  %s" + RESET + "%n",
                    "LUA SYNTAX", "JAVA EQUIVALENT", "LESSON");
            divider(80);
            cheatRow("local x = 10",                "int x = 10;",                       "8");
            cheatRow("tonumber(ARGV[1])",            "Integer.parseInt(args[0])",          "3");
            cheatRow("tonumber(val) or 0",           "val != null ? val : 0",              "5");
            cheatRow("a .. b",                       "a + b  (String concat)",             "2");
            cheatRow("math.floor(a/b)",              "Math.floor(a/b)",                    "3");
            cheatRow("math.min(a,b)",                "Math.min(a,b)",                      "3");
            cheatRow("if x > 0 then...end",          "if (x > 0) { ... }",                "6");
            cheatRow("return {1, count, 'ok'}",      "return List.of(1L, count, 'ok')",   "7");
            cheatRow("redis.call('GET', KEYS[1])",   "jedis.get(key)",                     "4");
            cheatRow("redis.call('SET', k, v)",      "jedis.set(key, value)",              "4");
            cheatRow("redis.call('INCR', KEYS[1])",  "jedis.incr(key)",                    "6");
            cheatRow("redis.call('EXPIRE', k, t)",   "jedis.expire(key, ttl)",             "4");
            System.out.println();

            jedis.flushDB();
            System.out.println(DIM + "  cleanup done — all lesson keys deleted" + RESET);
            System.out.println();
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    static void header(String title) {
        System.out.println();
        System.out.println(BOLD + BLUE +
                "╔══════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + BLUE + "║  " + RESET + BOLD + title + RESET);
        System.out.println(BOLD + BLUE +
                "╚══════════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    static void divider(int len) {
        System.out.println(DIM + "  " + "─".repeat(len) + RESET);
    }

    static void concept(String... lines) {
        System.out.println("  " + MAGENTA + "Concept:" + RESET);
        for (String line : lines)
            System.out.println("  " + CYAN + "▸ " + RESET + line);
        System.out.println();
    }

    static void luaBlock(String lua) {
        System.out.println("  " + YELLOW + "Lua script:" + RESET);
        for (String line : lua.strip().split("\n"))
            System.out.println(DIM + "  │  " + RESET + YELLOW + line + RESET);
        System.out.println();
    }

    static void javaCall(String call) {
        System.out.println(DIM + "  " + call + RESET);
    }

    static void output(String label, Object val) {
        System.out.println();
        System.out.printf("  " + GREEN + "%-20s" + RESET + " → " + BOLD + "%s" + RESET + "%n",
                label, val);
    }

    static void typeOf(String var, Object val) {
        System.out.printf("  " + DIM + "%-20s → Java type: %s" + RESET + "%n",
                "typeof " + var, val.getClass().getSimpleName());
    }

    static void warn(String msg) {
        System.out.println("  " + YELLOW + "⚠  " + msg + RESET);
    }

    static void cheatRow(String lua, String java, String lesson) {
        System.out.printf("  " + YELLOW + "%-32s" + RESET +
                CYAN   + "%-34s" + RESET +
                DIM    + "Lesson %s" + RESET + "%n",
                lua, java, lesson);
    }

    static String bar(int level, int capacity, int width) {
        int filled = capacity > 0 ? (int) ((level / (double) capacity) * width) : 0;
        String color = filled > (width * 7 / 10) ? GREEN
                     : filled > (width * 3 / 10) ? YELLOW : RED;
        return color + "█".repeat(Math.max(0, filled)) + RESET +
               DIM   + "░".repeat(Math.max(0, width - filled)) + RESET;
    }
}