package com.assignments.general;

import java.util.ArrayList;

public class ArrayListOperationInConstantTime {

        public static void main(String[] args) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(100);
            list.add(200);
            list.add(300);
            list.add(400);
            System.out.println(list);
            list.set(1,2);

            System.out.println(list.get(1));
            System.out.println(list);
            //remove index 1 ?
            list.set(1,list.get(list.size()-1)); // put list last element on index 2
            list.remove(list.size()-1);
            System.out.println(list);// remove last element.
        }
}
