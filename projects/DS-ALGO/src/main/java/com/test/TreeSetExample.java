package com.test;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class TreeSetExample {
	
	public static void main(String args[]) {
		
		
		TreeSet<Integer> set = new TreeSet<Integer>();
		
		set.add(10);
		set.add(-100);
		set.add(2);
		set.add(6);
		set.add(90);
		set.add(100);
		
		System.out.println((new ArrayList<>(set).toString()));
		System.out.println("Last is "+set.last());
		System.out.println("First is "+set.first());
		
	}
}
