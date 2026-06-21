package com.assignments.LinkedList;

public class ListReverseNodeinKGroupTest {


      public static class ListNode {
          int val;
          ListNode next;
          ListNode() {}
          ListNode(int val) { this.val = val; }
          ListNode(int val, ListNode next) { this.val = val; this.next = next; }
      }



    public static ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode headOfGroup = dummy;

        while(true) {

            ListNode kth = getKthNode(headOfGroup, k);
            if (kth == null)
                return dummy.next;


            ListNode nextGroupFirstNode = kth.next;
            ListNode oldGroupHead = headOfGroup.next;


            ListNode prev = nextGroupFirstNode;
            ListNode curr = headOfGroup.next;

            for (int x = 0; x < k; x++) {
                ListNode temp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = temp;
            }
            headOfGroup.next = kth;
            headOfGroup = oldGroupHead;
        }
    }

    private static ListNode getKthNode(ListNode curr, int k) {
        while (curr != null && k > 0) {
            curr = curr.next;
            k--;
        }
        return curr;
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
            head.next.next.next.next.next.next= new ListNode(7);


            printList(head);
            System.out.println(" ");
            ListNode head1 = reverseKGroup(head , 3);
            System.out.println(" ");
            printList(head1);
        }
}
