package com.assignments.general;

public class FindSecondLargestElement {

        public static int second(int a[],int n) {
            int max = a[0];
            int secondMax = max;
            for(int i=0;i<n;i++) {
               if(a[i] > max){
                   secondMax = max;
                   max=a[i];
               }
               else if(a[i] > secondMax){
                   secondMax = a[i];
               }
            }
            return secondMax;
        }

        public static void main(String[] args) {
            int[] arr = {1,3,2,5,3};
            int n = 5;
            int result = second(arr,n);
            System.out.println(result);
        }
}
