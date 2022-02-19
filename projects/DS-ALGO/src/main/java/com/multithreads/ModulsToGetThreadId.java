package com.multithreads;

public class ModulsToGetThreadId {
	
	public static void main(String args[]) {

		int TOTAL_THREADS = 5;
		for(int threadId=0; threadId < 5; threadId ++) {
			
			System.out.println(threadId % TOTAL_THREADS);
		}
		
	}

}
