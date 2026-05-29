package com.assignments.javads.heap;

import java.util.PriorityQueue;

public class PriorityQueueExample {

    private PriorityQueue queue = new PriorityQueue<Integer>((a,b)-> (a-b));

    public static void main(String args[]){

        PriorityQueueExample obj = new PriorityQueueExample();
        for(int x = 1; x < 5; x++){
            obj.queue.offer(x);
        }
        System.out.println("peek ===============");
        for(int x = 1; x < 5; x++){
            System.out.println(obj.queue.peek());
        }
        System.out.println("POLL MIN Heap ===============");
        for(int x = 1; x < 5; x++){

            System.out.println(obj.queue.poll());
        }
    }
}
