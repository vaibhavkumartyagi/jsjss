package com.test;

import java.util.TreeSet;

public class FarthestElementFromZero {
	
	public static void main(String args[]) {
		
		int arr[] = {-10,1,3,4,6,10};
		System.out.println(farthestfromzero(arr.length,arr));
		
	}

	public static int farthestfromzero(int N, int [] Arr) {
	    TreeSet<Integer> ts = new TreeSet<Integer>();
	    for (int i=0; i<N; i++){
	          ts.add(Arr[i]);
	    } 
	  int maxV = ts.last();
	  int minV = ts.first();
	  
	  if(Math.abs(minV) >= maxV){
	      return minV;
	  }
	  return maxV;

	}
}
