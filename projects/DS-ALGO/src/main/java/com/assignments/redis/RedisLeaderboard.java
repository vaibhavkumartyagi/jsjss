package com.assignments.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import java.util.List;

public class RedisLeaderboard {

    static final String KEY   = "stocks";
    static final String RESET = "\u001B[0m";
    static final String BOLD  = "\u001B[1m";
    static final String CYAN  = "\u001B[36m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW= "\u001B[33m";
    static final String RED   = "\u001B[31m";
    static final String BLUE  = "\u001B[34m";
    static final String DIM   = "\u001B[2m";

    public static void main(String[] args) {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            jedis.del(KEY);

            // ═══════════════════════════════════════════════════════
            // STEP 1 — ZADD : add stocks with volume scores
            // ═══════════════════════════════════════════════════════
            header("STEP 1 — ZADD  :  add members with scores");

            jedis.zadd(KEY,  5000, "RELIANCE");
            jedis.zadd(KEY,  1200, "TCS");
            jedis.zadd(KEY,  8700, "INFY");
            jedis.zadd(KEY,  3400, "HDFC");
            jedis.zadd(KEY,  9100, "WIPRO");
            jedis.zadd(KEY,  6500, "BAJAJ");
            jedis.zadd(KEY,  2100, "AXIS");

            System.out.println(DIM + "  jedis.zadd(\"stocks\", 5000, \"RELIANCE\")  ← score=5000" + RESET);
            System.out.println(DIM + "  jedis.zadd(\"stocks\", 1200, \"TCS\")       ← score=1200" + RESET);
            System.out.println(DIM + "  jedis.zadd(\"stocks\", 8700, \"INFY\")      ← score=8700" + RESET);
            System.out.println(DIM + "  jedis.zadd(\"stocks\", 3400, \"HDFC\")      ← score=3400" + RESET);
            System.out.println(DIM + "  jedis.zadd(\"stocks\", 9100, \"WIPRO\")     ← score=9100" + RESET);
            System.out.println(DIM + "  jedis.zadd(\"stocks\", 6500, \"BAJAJ\")     ← score=6500" + RESET);
            System.out.println(DIM + "  jedis.zadd(\"stocks\", 2100, \"AXIS\")      ← score=2100" + RESET);
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 2 — ZRANGE : all members sorted asc (lowest first)
            // ═══════════════════════════════════════════════════════
            header("STEP 2 — ZRANGE  :  all members, lowest score first");
            System.out.println(DIM + "  jedis.zrangeWithScores(\"stocks\", 0, -1)" + RESET);
            System.out.println();

            List<Tuple> all = jedis.zrangeWithScores(KEY, 0, -1);
            printLeaderboard(all, false);

            // ═══════════════════════════════════════════════════════
            // STEP 3 — ZINCRBY : simulate volume update on new tick
            // ═══════════════════════════════════════════════════════
            header("STEP 3 — ZINCRBY  :  update scores on new tick");
            System.out.println(DIM + "  jedis.zincrby(\"stocks\", 4000, \"TCS\")    ← TCS  1200 + 4000" + RESET);
            System.out.println(DIM + "  jedis.zincrby(\"stocks\", 1500, \"AXIS\")   ← AXIS 2100 + 1500" + RESET);
            System.out.println(DIM + "  jedis.zincrby(\"stocks\",  800, \"HDFC\")   ← HDFC 3400 +  800" + RESET);
            System.out.println();

            double tcsFinal   = jedis.zincrby(KEY, 4000, "TCS");
            double axisFinal  = jedis.zincrby(KEY, 1500, "AXIS");
            double hdfcFinal  = jedis.zincrby(KEY,  800, "HDFC");

            row("ZINCRBY", "TCS",  "1200 + 4000", tcsFinal);
            row("ZINCRBY", "AXIS", "2100 + 1500", axisFinal);
            row("ZINCRBY", "HDFC", "3400 +  800", hdfcFinal);

            // ═══════════════════════════════════════════════════════
            // STEP 4 — ZRANGE again : see updated leaderboard
            // ═══════════════════════════════════════════════════════
            header("STEP 4 — ZRANGE (after ZINCRBY)  :  revised leaderboard");
            System.out.println(DIM + "  jedis.zrangeWithScores(\"stocks\", 0, -1)" + RESET);
            System.out.println();

            List<Tuple> updated = jedis.zrangeWithScores(KEY, 0, -1);
            printLeaderboard(updated, false);

            // ═══════════════════════════════════════════════════════
            // STEP 5 — ZREVRANGE : top N leaderboard (highest first)
            // ═══════════════════════════════════════════════════════
            header("STEP 5 — ZREVRANGE  :  TOP 3 leaderboard (highest first)");
            System.out.println(DIM + "  jedis.zrevrangeWithScores(\"stocks\", 0, 2)   ← rank 0,1,2 from top" + RESET);
            System.out.println();

            List<Tuple> top3 = jedis.zrevrangeWithScores(KEY, 0, 2);
            printLeaderboard(top3, true);

            // ═══════════════════════════════════════════════════════
            // STEP 6 — ZRANK / ZREVRANK : where does a stock stand?
            // ═══════════════════════════════════════════════════════
            header("STEP 6 — ZRANK / ZREVRANK  :  position check");
            System.out.println(DIM + "  jedis.zrank(\"stocks\", member)     ← 0 = lowest score" + RESET);
            System.out.println(DIM + "  jedis.zrevrank(\"stocks\", member)  ← 0 = highest score" + RESET);
            System.out.println();

            String[] members = {"WIPRO", "TCS", "RELIANCE", "AXIS"};
            System.out.printf("  %-12s %10s %12s %10s%n",
                    "STOCK", "SCORE", "RANK(asc)", "RANK(desc)");
            divider(50);
            for (String m : members) {
                Double  s  = jedis.zscore(KEY, m);
                Long    r  = jedis.zrank(KEY, m);
                Long    rr = jedis.zrevrank(KEY, m);
                System.out.printf("  " + CYAN + "%-12s" + RESET +
                        YELLOW + " %10.0f" + RESET +
                        GREEN  + " %12d" + RESET +
                        RED    + " %10d" + RESET + "%n",
                        m, s, r, rr);
            }
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // STEP 7 — ZRANGEBYSCORE : stocks in a volume band
            // ═══════════════════════════════════════════════════════
            header("STEP 7 — ZRANGEBYSCORE  :  stocks between score 3000 and 7000");
            System.out.println(DIM + "  jedis.zrangeByScoreWithScores(\"stocks\", 3000, 7000)" + RESET);
            System.out.println();

            List<Tuple> band = jedis.zrangeByScoreWithScores(KEY, 3000, 7000);
            printLeaderboard(band, false);

            // ═══════════════════════════════════════════════════════
            // STEP 8 — ZREVRANGEBYSCORE : same band, highest first
            //          ⚠️  args are (max, min) — reversed!
            // ═══════════════════════════════════════════════════════
            header("STEP 8 — ZREVRANGEBYSCORE  :  same band, highest first");
            System.out.println(DIM + "  jedis.zrevrangeByScoreWithScores(\"stocks\", 7000, 3000)" + RESET);
            System.out.println(YELLOW + "  ⚠  args are (MAX, MIN) — reversed compared to zrangebyscore!" + RESET);
            System.out.println();

            List<Tuple> bandDesc = jedis.zrevrangeByScoreWithScores(KEY, 7000, 3000);
            printLeaderboard(bandDesc, true);

            // ═══════════════════════════════════════════════════════
            // STEP 9 — ZSCORE + ZCARD : quick lookups
            // ═══════════════════════════════════════════════════════
            header("STEP 9 — ZSCORE + ZCARD  :  quick lookups");
            System.out.println(DIM + "  jedis.zscore(\"stocks\", \"INFY\")  ← get score of one member" + RESET);
            System.out.println(DIM + "  jedis.zcard(\"stocks\")           ← total members (cardinality)" + RESET);
            System.out.println();

            System.out.println("  " + CYAN + "ZSCORE" + RESET + " INFY  → " +
                    YELLOW + jedis.zscore(KEY, "INFY") + RESET);
            System.out.println("  " + CYAN + "ZSCORE" + RESET + " WIPRO → " +
                    YELLOW + jedis.zscore(KEY, "WIPRO") + RESET);
            System.out.println("  " + CYAN + "ZCARD" + RESET + "        → " +
                    GREEN + jedis.zcard(KEY) + " members total" + RESET);
            System.out.println();

            // ═══════════════════════════════════════════════════════
            // FINAL — full leaderboard summary
            // ═══════════════════════════════════════════════════════
            header("FINAL — complete leaderboard  (highest volume = #1)");
            System.out.println(DIM + "  jedis.zrevrangeWithScores(\"stocks\", 0, -1)" + RESET);
            System.out.println();

            List<Tuple> finalBoard = jedis.zrevrangeWithScores(KEY, 0, -1);
            System.out.printf("  %-6s %-14s %10s %10s%n", "RANK", "STOCK", "VOLUME", "BAR");
            divider(55);
            int pos = 1;
            for (Tuple t : finalBoard) {
                String bar = bar((int) t.getScore(), 9100, 20);
                String color = pos == 1 ? YELLOW : pos <= 3 ? CYAN : RESET;
                System.out.printf("  " + color + "%-6s %-14s %10.0f  %s" + RESET + "%n",
                        "#" + pos, t.getElement(), t.getScore(), bar);
                pos++;
            }
            System.out.println();

            jedis.del(KEY);
            System.out.println(DIM + "  cleanup done — key deleted" + RESET);
            System.out.println();
        }
    }

    // ── helpers ──────────────────────────────────────────────────

    static void header(String title) {
        System.out.println();
        System.out.println(BOLD + BLUE +
                "╔══════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + BLUE + "║  " + RESET + BOLD + title + RESET);
        System.out.println(BOLD + BLUE +
                "╚══════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    static void divider(int len) {
        System.out.println(DIM + "  " + "─".repeat(len) + RESET);
    }

    static void row(String cmd, String member, String expr, double result) {
        System.out.printf("  " + CYAN + "%-10s" + RESET +
                " %-10s  " + DIM + "%-18s" + RESET +
                " → " + GREEN + "%.1f" + RESET + "%n",
                cmd, member, expr, result);
    }

    static void printLeaderboard(List<Tuple> list, boolean descOrder) {
        System.out.printf("  %-6s %-14s %10s%n", "POS", "STOCK", "SCORE");
        divider(36);
        int pos = descOrder ? 1 : list.size();
        for (Tuple t : list) {
            String bar   = bar((int) t.getScore(), 9100, 16);
            String color = (descOrder && pos <= 3) || (!descOrder && pos >= list.size() - 2)
                           ? CYAN : RESET;
            System.out.printf("  " + color + "%-6s %-14s %10.0f  %s" + RESET + "%n",
                    (descOrder ? "#" + pos : "rank" + (pos - 1)),
                    t.getElement(), t.getScore(), bar);
            if (descOrder) pos++; else pos--;
        }
        System.out.println();
    }

    static String bar(int score, int max, int width) {
        int filled = (int) ((score / (double) max) * width);
        return GREEN + "█".repeat(Math.max(0, filled)) + RESET +
               DIM   + "░".repeat(Math.max(0, width - filled)) + RESET;
    }
}