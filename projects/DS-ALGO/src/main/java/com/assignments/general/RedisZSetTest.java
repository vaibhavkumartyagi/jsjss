package com.assignments.general;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.params.ZRangeParams;

import java.util.List;
import java.util.Map;

public class RedisZSetTest {

    public static void main(String[] args) {

        try (Jedis jedis = new Jedis("localhost", 6379)) {

            System.out.println("Connected: " + jedis.ping());

            // cleanup before test
            jedis.del("stocks");

            // -------------------------------------------------------
            // 1. ZADD — add members with scores
            // -------------------------------------------------------
            jedis.zadd("stocks", 5000, "RELIANCE");
            jedis.zadd("stocks", 1200, "TCS");
            jedis.zadd("stocks", 8700, "INFY");
            jedis.zadd("stocks", 3400, "HDFC");
            jedis.zadd("stocks", 9100, "WIPRO");
            System.out.println("\n--- ZADD done ---");

            // -------------------------------------------------------
            // 2. ZCARD — total count
            // -------------------------------------------------------
            long count = jedis.zcard("stocks");
            System.out.println("\nZCARD (total members): " + count);

            // -------------------------------------------------------
            // 3. ZSCORE — get score of a member
            // -------------------------------------------------------
            Double score = jedis.zscore("stocks", "INFY");
            System.out.println("\nZSCORE INFY: " + score);  // 8700.0

            // -------------------------------------------------------
            // 4. ZRANK — rank of member (0-based, lowest score = 0)
            // -------------------------------------------------------
            Long rank = jedis.zrank("stocks", "INFY");
            System.out.println("\nZRANK INFY (asc): " + rank);  // 3

            Long revRank = jedis.zrevrank("stocks", "INFY");
            System.out.println("ZREVRANK INFY (desc): " + revRank); // 1

            // -------------------------------------------------------
            // 5. ZRANGE — get members by rank range (asc)
            // -------------------------------------------------------
            List<String> all = jedis.zrange("stocks", 0, -1);
            System.out.println("\nZRANGE all (asc by score): " + all);
            // [TCS, HDFC, RELIANCE, INFY, WIPRO]

            // -------------------------------------------------------
            // 6. ZRANGE with scores
            // -------------------------------------------------------
            List<Tuple> allWithScores = jedis.zrangeWithScores("stocks", 0, -1);
            System.out.println("\nZRANGE with scores:");
            allWithScores.forEach(t ->
                System.out.println("  " + t.getElement() + " -> " + t.getScore())
            );

            // -------------------------------------------------------
            // 7. ZREVRANGE — get members by rank range (desc)
            // -------------------------------------------------------
            List<String> topThree = jedis.zrevrange("stocks", 0, 2);
            System.out.println("\nZREVRANGE top 3 (desc): " + topThree);
            // [WIPRO, INFY, RELIANCE]

            // -------------------------------------------------------
            // 8. ZRANGEBYSCORE — get members between score range
            // -------------------------------------------------------
            List<String> midRange = jedis.zrangeByScore("stocks", 3000, 8000);
            System.out.println("\nZRANGEBYSCORE 3000-8000: " + midRange);
            // [HDFC, RELIANCE, INFY... depending on scores]

            // -------------------------------------------------------
            // 9. ZRANGEBYSCORE with scores
            // -------------------------------------------------------
            List<Tuple> midWithScores = jedis.zrangeByScoreWithScores("stocks", 3000, 9000);
            System.out.println("\nZRANGEBYSCORE with scores:");
            midWithScores.forEach(t ->
                System.out.println("  " + t.getElement() + " -> " + t.getScore())
            );

            // -------------------------------------------------------
            // 10. ZINCRBY — increment score of a member
            // -------------------------------------------------------
            double newScore = jedis.zincrby("stocks", 500, "TCS");
            System.out.println("\nZINCRBY TCS +500 → new score: " + newScore); // 1700.0

            // -------------------------------------------------------
            // 11. ZCOUNT — count members in score range
            // -------------------------------------------------------
            long cnt = jedis.zcount("stocks", 1000, 6000);
            System.out.println("\nZCOUNT 1000-6000: " + cnt);

            // -------------------------------------------------------
            // 12. ZPOPMIN / ZPOPMAX — remove and return lowest/highest
            // -------------------------------------------------------
            List<Tuple> min = jedis.zpopmin("stocks", 1);
            System.out.println("\nZPOPMIN (lowest score removed): "
                + min.get(0).getElement() + " -> " + min.get(0).getScore());

            List<Tuple> max = jedis.zpopmax("stocks", 1);
            System.out.println("ZPOPMAX (highest score removed): "
                + max.get(0).getElement() + " -> " + max.get(0).getScore());

            // -------------------------------------------------------
            // 13. ZREM — remove a specific member
            // -------------------------------------------------------
            jedis.zrem("stocks", "HDFC");
            System.out.println("\nZREM HDFC done. Remaining: " + jedis.zrange("stocks", 0, -1));

            // -------------------------------------------------------
            // 14. ZREMRANGEBYRANK — remove by rank range
            // -------------------------------------------------------
            jedis.zadd("stocks", 100, "DUMMY1");
            jedis.zadd("stocks", 200, "DUMMY2");
            jedis.zremrangeByRank("stocks", 0, 1); // remove 2 lowest ranked
            System.out.println("\nAfter ZREMRANGEBYRANK (removed 2 lowest): "
                + jedis.zrange("stocks", 0, -1));

            // -------------------------------------------------------
            // 15. ZREMRANGEBYSCORE — remove by score range
            // -------------------------------------------------------
            jedis.zadd("stocks", 100, "JUNK1");
            jedis.zadd("stocks", 150, "JUNK2");
            jedis.zremrangeByScore("stocks", 0, 200);
            System.out.println("\nAfter ZREMRANGEBYSCORE (removed score 0-200): "
                + jedis.zrange("stocks", 0, -1));

            // cleanup
            jedis.del("stocks");
            System.out.println("\n--- cleanup done ---");
        }
    }
}