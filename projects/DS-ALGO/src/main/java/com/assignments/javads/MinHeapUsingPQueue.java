package com.assignments.javads;

import java.util.PriorityQueue;

public class MinHeapUsingPQueue {

    private final PriorityQueue<Integer> queue = new PriorityQueue<Integer>((a, b)-> (a-b));

    public static void main(String args[]){

        MinHeapUsingPQueue obj = new MinHeapUsingPQueue();
        for(int x = 1; x < 5; x++){
            obj.queue.offer(x);
        }

        System.out.println("POLL MIN Heap ===============");
        for(int x = 1; x < 5; x++){

            System.out.println(obj.queue.poll());
        }
    }
}
