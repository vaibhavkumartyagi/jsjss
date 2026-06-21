package com.assignments.corejava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorUsingComparable {



    public static class UserCB implements Comparable<UserCB>{

        String name;
        int empId;
        Double salary;

        public UserCB(){

        }
        public UserCB(String name, int empId, Double salary){

            this.name=name;
            this.empId=empId;
            this.salary = salary;
        }

        @Override
        public int compareTo(UserCB o) {

          return   Comparator.comparingInt((UserCB c) -> c.empId).
                    thenComparing(c -> c.name).
                    thenComparing(c -> c.salary).
                    compare(this,o); // this is important
        }
    }



    public static void main (String [] args){


        List<UserCB> list = ComparatorUsingComparable.getMeListOfUserCB();
        Collections.sort(list);
        ComparatorUsingComparable.printListUserCB(list);
    }










    public static List<UserCB> getMeListOfUserCB() {
        List<UserCB> list = new ArrayList<>();

        // ── empId = 10 cluster (4 users) ─────────────────────────────
        // After sort: Aaron < Aaron (salary) < Arjun < Zara
        list.add(new UserCB("Zara Khan",      10, 150000.0));
        list.add(new UserCB("Aaron Mehta",    10,  90000.0));
        list.add(new UserCB("Arjun Sharma",   10, 130000.0));
        list.add(new UserCB("Aaron Mehta",    10,  75000.0));  // same name, lower salary

        // ── empId = 10 cluster (4 users) ─────────────────────────────
        // After sort: Aaron < Aaron (salary) < Arjun < Zara
        list.add(new UserCB("Zara Khan",      10, 150000.0));
        list.add(new UserCB("Aaron Mehta",    10,  90000.0));
        list.add(new UserCB("Arjun Sharma",   10, 130000.0));
        list.add(new UserCB("Aaron Mehta",    10,  75000.0));  // same name, lower salary

        // ── empId = 30 cluster (3 users) ─────────────────────────────
        // After sort: Amit < Priya < Priya (salary)
        list.add(new UserCB("Priya Nair",     30, 200000.0));
        list.add(new UserCB("Amit Verma",     30, 160000.0));
        list.add(new UserCB("Priya Nair",     30, 185000.0));  // same name, lower salary

        // ── empId = 55 cluster (2 users) ─────────────────────────────
        // After sort: Neha < Vikram
        list.add(new UserCB("Vikram Rao",     55, 310000.0));
        list.add(new UserCB("Neha Pillai",    55, 275000.0));

        // ── empId = 99 cluster (2 users) ─────────────────────────────
        // After sort: Suresh < Suresh (salary)
        list.add(new UserCB("Suresh Desai",   99, 500000.0));
        list.add(new UserCB("Suresh Desai",   99, 480000.0));  // exact same name, salary tiebreak only

        // ── Singletons for clean empId gaps ──────────────────────────
        list.add(new UserCB("Meera Iyer",      1,  45000.0));  // lowest empId, sorts first
        list.add(new UserCB("Pooja Tiwari",  100, 750000.0));  // highest empId, sorts last

        return list;
    }
    public static void printListUserCB(List<UserCB> list) {
        String border  = "+" + "-".repeat(20) + "+" + "-".repeat(8) + "+" + "-".repeat(14) + "+";
        String header  = String.format("| %-18s | %-6s | %-12s |", "Name", "EmpId", "Salary");

        System.out.println(border);
        System.out.println(header);
        System.out.println(border);

        for (UserCB u : list) {
            System.out.println(String.format("| %-18s | %-6d | %-12.2f |", u.name, u.empId, u.salary));
        }

        System.out.println(border);
        System.out.println("  Total Users: " + list.size());
        System.out.println(border);
    }
}
