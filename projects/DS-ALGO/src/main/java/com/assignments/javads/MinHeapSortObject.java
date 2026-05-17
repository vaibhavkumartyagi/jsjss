package com.assignments.javads;

import java.time.LocalTime;
import java.util.PriorityQueue;

public class MinHeapSortObject {

    static class StockFeed {
        String stockName;
        int volume;
        LocalTime time;
        StockFeed(String stockName, int volume, LocalTime time) {
            this.stockName = stockName;
            this.volume = volume;
            this.time = time;
        }
    }
    public static void main(String args[]){
        PriorityQueue<StockFeed> pq = new PriorityQueue<>(
                (a, b) -> a.volume - b.volume  // min-heap, lowest volume first
        );

        pq.offer(new StockFeed("RELIANCE", 5000, LocalTime.now()));
        pq.offer(new StockFeed("TCS",      1200, LocalTime.now()));
        pq.offer(new StockFeed("INFY",     8700, LocalTime.now()));
        while (!pq.isEmpty()) {
            StockFeed s = pq.poll();
            System.out.println(s.stockName + " -> " + s.volume);
        }
    }
}
