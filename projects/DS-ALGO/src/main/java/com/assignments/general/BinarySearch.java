package com.assignments.general;

// you can write to stdout for debugging purposes, e.g.
// System.out.println("this is a debug message");

class BinarySearch {
    public int solution(int [] N, int val) {

        return binarySearch(val,N , 0 , N.length-1);
    }

    private int binarySearch(int val,int [] arr, int start, int end){


        if(start >= end && arr[start] == val)
            return start;
        else if(start >= end )
            return -1;


        int mid = (start+end)/2 +1;

        if(arr[mid] == val)
            return mid;
        if(val < arr[mid])
            return binarySearch(val,arr,0,mid-1);
        else
            return binarySearch(val,arr, mid+1,end);

    }

    public static void main(String args[]){
        BinarySearch bg = new BinarySearch();
        int arr [] = {1,2,3,5,6,77,88,99,101,102};

        System.out.println(bg.solution(arr,88));
        System.out.println(bg.solution(arr,103));
    }
}
