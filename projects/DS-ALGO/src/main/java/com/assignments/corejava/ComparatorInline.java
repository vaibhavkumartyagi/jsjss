package com.assignments.corejava;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ComparatorInline {




    public static void main (String [] args){
        List<UserCR> list = ComparatorInline.getMeListOfUserCR();

        Comparator<UserCR> LIST_COMPARATOR_USERCR = Comparator.comparingInt((UserCR c) -> c.empId)
                .thenComparing(c -> c.name)
                .thenComparingDouble(c -> c.salary);

        list.sort(LIST_COMPARATOR_USERCR);
     // or this    Collections.sort(list,LIST_COMPARATOR_USERCR)
        ComparatorInline.printListUserCR(list);

        list = ComparatorInline.getMeListOfUserCR();

        List<UserCR> list2 =   list.stream()
                .sorted(Comparator.comparingInt((UserCR u) -> u.empId)
                        .thenComparing(u -> u.name)
                        .thenComparing(u -> u.salary)).toList();

        ComparatorInline.printListUserCR(list2);

        list = ComparatorInline.getMeListOfUserCR();
        Collections.sort(list, (u1, u2) ->
                Integer.compare(u1.empId, u2.empId) != 0 ? Integer.compare(u1.empId, u2.empId) :
                        u1.name.compareTo(u2.name)         != 0 ? u1.name.compareTo(u2.name) :
                                u1.salary.compareTo(u2.salary)
        );

        ComparatorInline.printListUserCR(list2);


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
/*
/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA CE.app/Contents/lib/idea_rt.jar=50057 -Dfile.encoding=UTF-8 -classpath /Users/vaibhav/Documents/Vaibhav-Disk/DS-Algo/projects/DS-ALGO/target/classes:/Users/vaibhav/.m2/repository/org/apache/logging/log4j/log4j-api/2.11.1/log4j-api-2.11.1.jar:/Users/vaibhav/.m2/repository/org/apache/logging/log4j/log4j-core/2.11.1/log4j-core-2.11.1.jar:/Users/vaibhav/.m2/repository/org/apache/logging/log4j/log4j-slf4j-impl/2.11.1/log4j-slf4j-impl-2.11.1.jar:/Users/vaibhav/.m2/repository/com/google/guava/guava/19.0/guava-19.0.jar:/Users/vaibhav/.m2/repository/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar:/Users/vaibhav/.m2/repository/redis/clients/jedis/7.5.0/jedis-7.5.0.jar:/Users/vaibhav/.m2/repository/org/apache/commons/commons-pool2/2.12.1/commons-pool2-2.12.1.jar:/Users/vaibhav/.m2/repository/org/json/json/20251224/json-20251224.jar:/Users/vaibhav/.m2/repository/redis/clients/authentication/redis-authx-core/0.1.1-beta2/redis-authx-core-0.1.1-beta2.jar:/Users/vaibhav/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/Users/vaibhav/.m2/repository/jetty/org.mortbay.jetty/5.1.4/org.mortbay.jetty-5.1.4.jar:/Users/vaibhav/.m2/repository/org/apache/commons/commons-lang3/3.0/commons-lang3-3.0.jar:/Users/vaibhav/.m2/repository/com/google/code/gson/gson/2.6.2/gson-2.6.2.jar:/Users/vaibhav/.m2/repository/commons-httpclient/commons-httpclient/3.0.1/commons-httpclient-3.0.1.jar:/Users/vaibhav/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar:/Users/vaibhav/.m2/repository/commons-logging/commons-logging/1.0.3/commons-logging-1.0.3.jar:/Users/vaibhav/.m2/repository/commons-codec/commons-codec/1.2/commons-codec-1.2.jar com.assignments.corejava.ComparatorInline
+--------------------+--------+--------------+
| Name               | EmpId  | Salary       |
+--------------------+--------+--------------+
| Meera Iyer         | 1      | 45000.00     |
| Aaron Mehta        | 10     | 75000.00     |
| Aaron Mehta        | 10     | 75000.00     |
| Aaron Mehta        | 10     | 90000.00     |
| Aaron Mehta        | 10     | 90000.00     |
| Arjun Sharma       | 10     | 130000.00    |
| Arjun Sharma       | 10     | 130000.00    |
| Zara Khan          | 10     | 150000.00    |
| Zara Khan          | 10     | 150000.00    |
| Amit Verma         | 30     | 160000.00    |
| Priya Nair         | 30     | 185000.00    |
| Priya Nair         | 30     | 200000.00    |
| Neha Pillai        | 55     | 275000.00    |
| Vikram Rao         | 55     | 310000.00    |
| Suresh Desai       | 99     | 480000.00    |
| Suresh Desai       | 99     | 500000.00    |
| Pooja Tiwari       | 100    | 750000.00    |
+--------------------+--------+--------------+
  Total Users: 17
+--------------------+--------+--------------+
+--------------------+--------+--------------+
| Name               | EmpId  | Salary       |
+--------------------+--------+--------------+
| Meera Iyer         | 1      | 45000.00     |
| Aaron Mehta        | 10     | 75000.00     |
| Aaron Mehta        | 10     | 75000.00     |
| Aaron Mehta        | 10     | 90000.00     |
| Aaron Mehta        | 10     | 90000.00     |
| Arjun Sharma       | 10     | 130000.00    |
| Arjun Sharma       | 10     | 130000.00    |
| Zara Khan          | 10     | 150000.00    |
| Zara Khan          | 10     | 150000.00    |
| Amit Verma         | 30     | 160000.00    |
| Priya Nair         | 30     | 185000.00    |
| Priya Nair         | 30     | 200000.00    |
| Neha Pillai        | 55     | 275000.00    |
| Vikram Rao         | 55     | 310000.00    |
| Suresh Desai       | 99     | 480000.00    |
| Suresh Desai       | 99     | 500000.00    |
| Pooja Tiwari       | 100    | 750000.00    |
+--------------------+--------+--------------+
  Total Users: 17
+--------------------+--------+--------------+
+--------------------+--------+--------------+
| Name               | EmpId  | Salary       |
+--------------------+--------+--------------+
| Meera Iyer         | 1      | 45000.00     |
| Aaron Mehta        | 10     | 75000.00     |
| Aaron Mehta        | 10     | 75000.00     |
| Aaron Mehta        | 10     | 90000.00     |
| Aaron Mehta        | 10     | 90000.00     |
| Arjun Sharma       | 10     | 130000.00    |
| Arjun Sharma       | 10     | 130000.00    |
| Zara Khan          | 10     | 150000.00    |
| Zara Khan          | 10     | 150000.00    |
| Amit Verma         | 30     | 160000.00    |
| Priya Nair         | 30     | 185000.00    |
| Priya Nair         | 30     | 200000.00    |
| Neha Pillai        | 55     | 275000.00    |
| Vikram Rao         | 55     | 310000.00    |
| Suresh Desai       | 99     | 480000.00    |
| Suresh Desai       | 99     | 500000.00    |
| Pooja Tiwari       | 100    | 750000.00    |
+--------------------+--------+--------------+
  Total Users: 17
+--------------------+--------+--------------+

Process finished with exit code 0


 */