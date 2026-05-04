package com.test;

import java.util.*;

// you can write to stdout for debugging purposes, e.g.
// System.out.println("this is a debug message");

class BinaryGap {
    public int solution(int N) {
        String b = getDecimalToBinary(N);

        int  right =0;
        Map<Character,Integer> map = new HashMap<Character,Integer>();
        int maxLen =0;

        while (right < b.length() ){
            char c = b.charAt(right);

            if( c == '1' && map.containsKey(c) ){
                maxLen = Math.max(maxLen, right-map.get(c)-1);
                map.put(c,right);
            }
            map.put(c,right);
            right++;
        }
        return maxLen;

    }

    private String getDecimalToBinary(int n){

        if (n == 0)
            return "0";

        StringBuilder binaryString = new StringBuilder();
        while(n > 0){
            int reminder = n % 2;
            binaryString.insert(0,reminder);
            n=n/2;
        }
        System.out.println(binaryString.toString());
        return binaryString.toString();
    }

    public static void main(String args[]){
        BinaryGap bg = new BinaryGap();
        System.out.println(bg.solution(9));
        System.out.println(bg.solution(1041));
    }
}
