package com.multithreads;

public class PrintNoInSequnenceWithoutWaitNotify implements Runnable{

	public PrinterThread printer;
	public int threadId;
	public int totalThreads;
	public static int MAX_VALUE= 12;
	
	public PrintNoInSequnenceWithoutWaitNotify(PrinterThread printer,int threadId , int totalThreads){
		this.printer = printer;
		this.threadId = threadId;
		this.totalThreads = totalThreads;
	}
	
	public void run() {
		
		while(printer.threadNo < PrintNoInSequnenceWithoutWaitNotify.MAX_VALUE) {
			printer.printNumber(threadId, totalThreads);
			
		}
	}
	
	public static void main(String args[]) {
		
			PrinterThread printer = new PrinterThread();
			
			Thread T1 = new Thread(new PrintNoInSequnenceWithoutWaitNotify(printer,0, 5));
			Thread T2 = new Thread(new PrintNoInSequnenceWithoutWaitNotify(printer,1, 5));
			Thread T3 = new Thread(new PrintNoInSequnenceWithoutWaitNotify(printer,2, 5));
			Thread T4 = new Thread(new PrintNoInSequnenceWithoutWaitNotify(printer,3, 5));
			Thread T5 = new Thread(new PrintNoInSequnenceWithoutWaitNotify(printer,4, 5));
			T1.start();
			T2.start();
			T3.start();
			T4.start();
			T5.start();
	}
	
}
