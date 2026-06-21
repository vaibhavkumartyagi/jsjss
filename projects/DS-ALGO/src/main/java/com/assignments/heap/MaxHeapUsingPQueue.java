package com.assignments.heap;

import java.util.PriorityQueue;

public class MaxHeapUsingPQueue {

    private final PriorityQueue<Integer> queue = new PriorityQueue<Integer>((a, b)-> (b-a));

    public static void main(String args[]){

        MaxHeapUsingPQueue obj = new MaxHeapUsingPQueue();
        for(int x = 1; x < 5; x++){
            obj.queue.offer(x);
        }
       
        System.out.println("POLL Max Heap (a,b)->  (b-a) ===============");
        for(int x = 1; x < 5; x++){

            System.out.println(obj.queue.poll());
        }
    }
}
