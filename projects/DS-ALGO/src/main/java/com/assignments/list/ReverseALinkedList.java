package com.assignments.list;

public class ReverseALinkedList {


      public static class ListNode {
          int val;
          ListNode next;
          ListNode() {}
          ListNode(int val) { this.val = val; }
          ListNode(int val, ListNode next) { this.val = val; this.next = next; }
      }


        public static ListNode reverseLinkedList(ListNode head, int k) {

            ListNode prev = null ;
            ListNode curr = head;

            while(curr != null ){

                ListNode temp = curr.next;  // preserver curr node -> Next
                curr.next = prev;   // reverse curr direction
                prev=curr; // make curr prev
                curr=temp; // make next curr
            }
            return prev;

        }


    private static void printList(ListNode curr){

        while(curr != null){
            System.out.print(curr.val+" ");
            curr = curr.next;
        }

    }

        public static void main(String args[]){

            ListNode head = new ListNode(1);
            head.next= new ListNode(2);
            head.next.next= new ListNode(3);
            head.next.next.next= new ListNode(4);
            head.next.next.next.next= new ListNode(5);
            head.next.next.next.next.next= new ListNode(6);

           // printList(head);
            ListNode head1 = reverseLinkedList(head , 3);
            System.out.println(" ");
            printList(head1);
        }
}
