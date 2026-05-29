package com.assignments.javads.heap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class HeapCreationTypes {
    public static void main(String args[]){

        PriorityQueue<Integer> minHeap1 = new PriorityQueue<Integer>();
        PriorityQueue<Integer> minHeap2 = new PriorityQueue<Integer>((a, b)-> (a-b));

        PriorityQueue<Integer> maxHeap1 = new PriorityQueue<Integer>((a, b)-> (b-a));
        PriorityQueue<Integer> maxHeap2 = new PriorityQueue<Integer>(Collections.reverseOrder());

        for(int x =0 ; x < 10; x ++){
           minHeap1.add(x);
           minHeap2.add(x);
           maxHeap1.add(x);
           maxHeap2.add(x);

        }

        System.out.println("MIN H "+new ArrayList<Integer>(minHeap1));
        System.out.println("MIN H "+new ArrayList<Integer>(minHeap2));
        System.out.println("MAX H "+new ArrayList<Integer>(maxHeap1));
        System.out.println("MAX H "+new ArrayList<Integer>(maxHeap2));

    }


}
