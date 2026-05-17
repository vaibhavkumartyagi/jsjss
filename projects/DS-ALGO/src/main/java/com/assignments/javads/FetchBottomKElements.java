package com.assignments.javads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class FetchBottomKElements {

    private final PriorityQueue<Integer> queue = new PriorityQueue<Integer>((a, b)-> (b-a));

    public static void main(String args[]){
        FetchBottomKElements obj = new FetchBottomKElements();
        System.out.println(obj.getTopK(10));
    }

    public List<Integer>  getTopK(int k) {
        List<Integer> list = new ArrayList<>();
        for (int x = 1; x < 500; x++) {
            queue.offer(x);
            if (queue.size() >= k) {
                queue.poll(); // remove elements from queue rear
            }
        }
        while(!queue.isEmpty()) { // add remaining elements in Array
            list.add(queue.poll());
        }
        System.out.println("For TOP K - MIN Heap will work (a,b)-> (b-a)");
        System.out.println("Last K elements in queue ");
        System.out.println(list);
        Collections.reverse(list);
        System.out.println("Last K elements in queue ,Decending order Collections.reverse(list)");
        return list;
    }
}
