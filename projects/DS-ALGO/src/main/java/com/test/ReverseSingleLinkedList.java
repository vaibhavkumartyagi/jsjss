package com.test;

public class ReverseSingleLinkedList {
	
	LinkNode head = null;
	LinkNode tail = null;
	
	public LinkNode reverse() {
		
		LinkNode prev ;
		LinkNode curr;
		LinkNode next ;
		
		prev = null;
		curr = head;
		next = head.next;
		
		while ( next != null) {
			
			curr.next = prev;
			prev = curr;
			curr = next;
			next = next.next;
		}
		
		head = prev;
		return prev;
	}
	
	public void addNode( int data) {
		
		LinkNode node  = new LinkNode(data);
		if(head == null) {
			head = node;
			tail = head;
		}else {
			
			tail.next = node;
			tail = node;
		}
	}
	
    public void printList( ) {
		
    	LinkNode head1 = null;
		
    	head1 = head;
		while(head1 != null) {
			System.out.print("["+head1.data+"] ");
			head1 = head1.next;
		}
	}
	
    
    public static void main(String args[]) {
    	
    	ReverseSingleLinkedList list = new ReverseSingleLinkedList();
    	
    	
    	for (int x=1; x<10; x++) {
    		
    		list.addNode(x);
    	}
    	
    	list.printList();
    	list.reverse();
    	list.printList();
    }

}
