package com.multithreads;

public class PrinterThread {

	
	public static int threadNo =0;
	
	
	public synchronized void printNumber(int threadId , int totlThreads) {
		
		if(threadNo % totlThreads == threadId) {
			System.out.println("ThreadId ["+threadId+"] "+ threadNo++);
		}
			
	}
}
