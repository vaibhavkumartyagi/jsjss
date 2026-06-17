package com.assignments.RateLimiter;

public class Temperature {

    private int noOfReadings;
    private double temperature;

    public void addTemperature(double value) {
        temperature += value;
        noOfReadings++;
    }

    public double getAvgTemperature() {
        if (noOfReadings == 0) {
            throw new IllegalStateException("No readings added yet");
        }
        return temperature / noOfReadings;
    }

    public int getNoOfReadings() {
        return noOfReadings;
    }

    public static void main(String[] args) throws InterruptedException {

        Temperature t = new Temperature();

        // Thread 1 - sends 50 readings of 10.0
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                t.addTemperature(10.0);
            }
        });

        // Thread 2 - sends 50 readings of 10.0
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                System.out.println("Average        : " + t.getAvgTemperature());
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Total readings : " + t.getNoOfReadings());
        System.out.println("Average        : " + t.getAvgTemperature());
        System.out.println("Expected avg   : 10.0");
        System.out.println("Expected reads : 100");
    }
}
