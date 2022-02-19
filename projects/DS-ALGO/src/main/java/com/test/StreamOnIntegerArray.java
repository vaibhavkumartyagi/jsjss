package com.test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamOnIntegerArray {

	
	public static void main(String[] args)
    {
        
		int[] arr = { 1, 2, 3, 4, 5 };
 
        List<Integer> list = Arrays.stream(arr)        // IntStream
                                    .boxed()          // Stream<Integer>
                                    .collect(Collectors.toList());
        
        List<Integer> list2 = Arrays.stream(arr).boxed().filter(x-> ( x%2 != 0  )).map(x -> 3*x).collect(Collectors.toList());
       
        System.out.println(list2);
        
    }
}
