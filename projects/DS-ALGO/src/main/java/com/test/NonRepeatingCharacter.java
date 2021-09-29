package com.test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NonRepeatingCharacter {

    public static void main(String[] args) {
	    nonrepeatingChar2("india");
    }

    public static Character nonrepeatingChar(String name){

        Map<Character,Integer> map = new LinkedHashMap<Character,Integer>();
        for(int x=0; x <name.length();x++){

            if(!map.containsKey(name.charAt(x))){
                map.put(name.charAt(x),1);
            }else
            {
                int val = map.get(name.charAt(x));
                val = val+1;
                map.put(name.charAt(x),val);
            }
        }

        for(Map.Entry<Character,Integer> entry : map.entrySet()){
            if(entry.getValue() ==1){
                System.out.println(entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }

    public static Character nonrepeatingChar2(String name){

    // Map<Object,Long> map =  name.chars().collect(Collectors.groupingBy(m -> Character.valueOf((char)m) ,Collectors.counting()));

//        for(Map.Entry<Character,Integer> entry : map.entrySet()){
//            if(entry.getValue() ==1){
//                System.out.println(entry.getKey());
//                return entry.getKey();
//            }
//        }
        return null;
    }
};
