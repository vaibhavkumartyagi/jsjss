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
		
		curr.next = prev;
		
		head = curr;
		return curr;
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
		System.out.print("\n ");
	}


     public int findMidElement() {
		
    	LinkNode ptr1 = null;
    	LinkNode ptr2 = null;
		
    	ptr1 = head;
    	ptr2 = head;
		while(ptr2.next != null) {
			
			if(ptr2.next != null)
			ptr2 = ptr2.next;
			if(ptr2.next != null)
			ptr2 = ptr2.next;
			
			ptr1 = ptr1.next;
		}
		return ptr1.data;
	}
    
     public  LinkNode reverse( int m, int n)
     {
    	 LinkNode prev = null;
    	 LinkNode curr = head;
  
         // 1. Skip the first `m` nodes
         for (int i = 1; curr != null && i < m; i++)
         {
             prev = curr;
             curr = curr.next;
         }
  
         // prev now points to position the (m-1)'th node
         // `curr` now points to position the m'th node
  
         LinkNode start = curr;
         LinkNode end = null;
  
         // 2. Traverse and reverse the sublist from position `m` to `n`
         for (int i = 1; curr != null && i <= n - m + 1; i++)
         {
             // Take note of the next node
        	 LinkNode next = curr.next;
  
             // move the current node onto the `end`
             curr.next = end;
             end = curr;
  
             // move to the next node
             curr = next;
         }
  
         /* `start` points to the m'th node
         `end` now points to the n'th node
         `curr` now points to the (n+1)'th node */
  
         // 3. Fix the pointers and return the head node
         start.next = curr;
         if (prev != null) {
             prev.next = end;
         }
         else {
             head = end;     // when `m = 1` (`prev` is null)
         }
  
         return head;
     }
     public void  reverseBetween(int left, int right) {
         
         int i=1;
         
         LinkNode newHead = head;
        // LinkNode newHead = head;
         LinkNode oldTail = null;
        
         LinkNode oldLeftNode = null; 
         LinkNode next = null;
         LinkNode prev = null;
        
         
         while(i < left){
             oldTail = newHead;
             newHead = newHead.next;   // New head point to 3 Now
             i++;
         }
         System.out.println("Nead head data: "+newHead.data+" i="+i);
         
        LinkNode start = newHead;
        
        	 oldLeftNode = newHead; 
         
         prev = newHead;
         newHead = newHead.next;
         
         while(i < right){
        	 next = newHead.next;
        	 newHead.next = prev;
        	 prev = newHead;
        	 newHead = next;
             i++;
         }
         
         //System.out.println("oldTail data: "+oldTail.data+" i="+i);
         System.out.println("Nead prev data: "+prev.data+" i="+i);
         System.out.println("Nead oldLeftNode data: "+oldLeftNode.data+" i="+i);
        
         oldLeftNode.next = newHead;
         
         if(oldTail != null)
         oldTail.next = prev;
         else
        	 head=prev;
         
        
         
     }
    public static void main(String args[]) {
    	
    	ReverseSingleLinkedList list = new ReverseSingleLinkedList();
    	
    	
//    	for (int x=1; x<11; x++) {
//    		
//    		list.addNode(x);
//    	}
    	
    	list.addNode(3);
    	list.addNode(5);
    	
    	list.printList();
    	int midElement = list.findMidElement();
    	System.out.println("midElement is ["+midElement+"]");
    	//list.reverse();
    	//list.printList();
    //	list.reverse();
    	//list.printList();
    	list.reverseBetween(1, 2);
        list.printList();
    }

}
