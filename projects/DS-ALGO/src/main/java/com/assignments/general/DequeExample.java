package com.assignments.general;

import java.util.ArrayDeque;
import java.util.Deque;

public class DequeExample {
    public static void main(String[] args) {
        Deque<String> deque = new ArrayDeque<>();

        for(int x=10; x < 70; x++) {
            deque.addLast("Element "+x);

        }
        System.out.println("Deque: " + deque);
        System.out.println("First Element: " + deque.getFirst());
        System.out.println("Last Element: " + deque.getLast());
        System.out.println("Last peekFirst: " + deque.peekFirst());
        System.out.println("Last removeFirst: " + deque.removeFirst());
        System.out.println("Last peekFirst: " + deque.peekFirst());
        deque.removeFirst();
        deque.removeLast();
        System.out.println("Deque after removal: " + deque);
    }
}