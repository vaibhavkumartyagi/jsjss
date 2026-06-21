package com.assignments.corejava;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ComparatorInterface {


    public static class UserCBComparator implements Comparator<UserCR>{


        @Override
        public int compare(UserCR u1, UserCR u2) {
            int empCmp = Integer.compare(u1.empId, u2.empId);  // fixed overflow
            if (empCmp != 0) return empCmp;

            int nameCmp = u1.name.compareTo(u2.name);
            if (nameCmp != 0) return nameCmp;

            return u1.salary.compareTo(u2.salary);
        }

        public int compare1(UserCB u1, UserCB u2){
            int emComparator = Integer.compare(u1.empId,u2.empId);

            if(emComparator ==0){
                int nameCom = u1.name.compareTo(u2.name);

                if(nameCom ==0){
                    return u1.salary.compareTo(u2.salary);

                }else{
                    return nameCom;
                }

            }else{
                return emComparator;
            }
        }

    }

    public static void main (String [] args){
        List<UserCR> list = ComparatorInline.getMeListOfUserCR();
        Collections.sort(list, new ComparatorInterface.UserCBComparator());
        ComparatorInline.printListUserCR(list);
    }

















    public static List<UserCR> getMeListOfUserCR() {
        List<UserCR> list = new ArrayList<>();

        // ── empId = 10 cluster (4 users) ─────────────────────────────
        // After sort: Aaron < Aaron (salary) < Arjun < Zara
        list.add(new UserCR("Zara Khan",      10, 150000.0));
        list.add(new UserCR("Aaron Mehta",    10,  90000.0));
        list.add(new UserCR("Arjun Sharma",   10, 130000.0));
        list.add(new UserCR("Aaron Mehta",    10,  75000.0));  // same name, lower salary

        // ── empId = 10 cluster (4 users) ─────────────────────────────
        // After sort: Aaron < Aaron (salary) < Arjun < Zara
        list.add(new UserCR("Zara Khan",      10, 150000.0));
        list.add(new UserCR("Aaron Mehta",    10,  90000.0));
        list.add(new UserCR("Arjun Sharma",   10, 130000.0));
        list.add(new UserCR("Aaron Mehta",    10,  75000.0));  // same name, lower salary

        // ── empId = 30 cluster (3 users) ─────────────────────────────
        // After sort: Amit < Priya < Priya (salary)
        list.add(new UserCR("Priya Nair",     30, 200000.0));
        list.add(new UserCR("Amit Verma",     30, 160000.0));
        list.add(new UserCR("Priya Nair",     30, 185000.0));  // same name, lower salary

        // ── empId = 55 cluster (2 users) ─────────────────────────────
        // After sort: Neha < Vikram
        list.add(new UserCR("Vikram Rao",     55, 310000.0));
        list.add(new UserCR("Neha Pillai",    55, 275000.0));

        // ── empId = 99 cluster (2 users) ─────────────────────────────
        // After sort: Suresh < Suresh (salary)
        list.add(new UserCR("Suresh Desai",   99, 500000.0));
        list.add(new UserCR("Suresh Desai",   99, 480000.0));  // exact same name, salary tiebreak only

        // ── Singletons for clean empId gaps ──────────────────────────
        list.add(new UserCR("Meera Iyer",      1,  45000.0));  // lowest empId, sorts first
        list.add(new UserCR("Pooja Tiwari",  100, 750000.0));  // highest empId, sorts last

        return list;
    }
    public static void printListUserCR(List<UserCR> list) {
        String border  = "+" + "-".repeat(20) + "+" + "-".repeat(8) + "+" + "-".repeat(14) + "+";
        String header  = String.format("| %-18s | %-6s | %-12s |", "Name", "EmpId", "Salary");

        System.out.println(border);
        System.out.println(header);
        System.out.println(border);

        for (UserCR u : list) {
            System.out.println(String.format("| %-18s | %-6d | %-12.2f |", u.name, u.empId, u.salary));
        }

        System.out.println(border);
        System.out.println("  Total Users: " + list.size());
        System.out.println(border);
    }
}
