package com.test;

import java.util.*;

public class SimultaneousLoginFinderMobiKiwik {

    record Event(String userId, String time, String type) {}
    
    record Window(String startTime, String endTime) {
        @Override
        public String toString() {
            return "Window{start=" + startTime + ", end=" + endTime + "}";
        }
    }

    public static List<Window> findAllWindows(List<Event> events) {
        List<Window> result = new ArrayList<>();

        // Step 1: count unique users
        Set<String> uniqueUsers = new HashSet<>();
        for (Event e : events) uniqueUsers.add(e.userId());
        int totalUniqueUsers = uniqueUsers.size();

        // Step 2: sort by time
        List<Event> sorted = new ArrayList<>(events);
        sorted.sort(Comparator.comparingInt(e -> parseMinutes(e.time())));

        // Step 3: sweep
        Set<String> activeUsers = new HashSet<>();
        String windowStart = null;

        for (Event e : sorted) {
            if (e.type().equals("logged_in")) {
                activeUsers.add(e.userId());

                // All users active → window opens
                if (activeUsers.size() == totalUniqueUsers && windowStart == null) {
                    windowStart = e.time();
                }

            } else { // logged_out
                // If window was open → close it and record
                if (windowStart != null) {
                    result.add(new Window(windowStart, e.time()));
                    windowStart = null;
                }
                activeUsers.remove(e.userId());
            }
        }

        return result;
    }

    private static int parseMinutes(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int min  = Integer.parseInt(time.substring(3, 5));
        boolean isPm = time.endsWith("pm");
        if (isPm && hour != 12) hour += 12;
        if (!isPm && hour == 12) hour = 0;
        return hour * 60 + min;
    }

    public static void main(String[] args) {
        List<Event> events = List.of(
            new Event("U1", "00:20am", "logged_in"),
            new Event("U1", "04:00am", "logged_in"),
            new Event("U2", "05:00am", "logged_out"),
            new Event("U3", "05:20am", "logged_out"),
            new Event("U1", "05:41am", "logged_out"),
            new Event("U2", "00:40am", "logged_in"),
            new Event("U1", "01:40am", "logged_out"),
            new Event("U3", "02:00am", "logged_in")
        );

        List<Window> windows = findAllWindows(events);
        windows.forEach(System.out::println);
    }
}