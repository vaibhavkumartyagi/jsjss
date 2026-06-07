package com.assignments.hw;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LongestNonRepeatingCharSequence {

	public static void main(String args[]) {
		
		int len = findLongestCharSequence("abcdefghhijklmnopqrst".toCharArray());
		System.out.println("Len is ["+len+"]");
	}

    public static int findLongestCharSequence(char [] charSequence) {

        int i=0 , j=0 ;
        int len = charSequence.length;
        Map<Character,Integer> map = new HashMap<Character,Integer>();
        int max=0;

        while(i < len ){

            Character c = charSequence[i];

            if(map.containsKey(c) && j < map.get(c)){

                j = map.get(c) +1;
            }
            max = Math.max(max , i-j+1);
            map.put(c,i);
            i++;
        }
        return max;

    }
	public static int findLongestCharSequence1(char [] charSequence) {
		
		
		int i= 0;
		int j= 0;
		int max =0;
		HashSet<Character> set = new HashSet<>(); 
		HashSet<Character> finalSet = null;
		for( i=0 ; i < charSequence.length ; i++) {
			
			for( j=i ; j < charSequence.length ; j++) {
				
				set.add(charSequence[j]);
				
				if(set.size() > max && set.size() == (j-i)+1) {
					max = set.size();
					finalSet = set;
				}
				
				if(set.size() < (j-i)+1) {
					set = new HashSet<>(); 
					break;
				}
			}
			
		}
		System.out.println("Input string is ["+Arrays.toString(charSequence)+"] char string is: "+finalSet.toString());
		return max;
	}
}