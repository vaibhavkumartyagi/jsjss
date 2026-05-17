package com.assignments.multithreads;

public class DeadlockDemo {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void method1() {
        synchronized (lock1) {
            System.out.println(Thread.currentThread().getName() + " acquired lock1");

            // Simulate some work
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            System.out.println(Thread.currentThread().getName() + " waiting for lock2");
            synchronized (lock2) {
                System.out.println(Thread.currentThread().getName() + " acquired lock2");
            }
        }
    }

    public void method2() {
        synchronized (lock2) {  // OPPOSITE ORDER!
            System.out.println(Thread.currentThread().getName() + " acquired lock2");

            try { Thread.sleep(100); } catch (InterruptedException e) {}

            System.out.println(Thread.currentThread().getName() + " waiting for lock1");
            synchronized (lock1) {
                System.out.println(Thread.currentThread().getName() + " acquired lock1");
            }
        }
    }

    public static void main(String[] args) {
        DeadlockDemo demo = new DeadlockDemo();

        Thread t1 = new Thread(demo::method1, "Thread-1");
        Thread t2 = new Thread(demo::method2, "Thread-2");

        t1.start();
        t2.start();
    }
}