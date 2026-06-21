package com.assignments.LinkedList;

public class ListReverseNodeinKGroup {


      public static class ListNode {
          int val;
          ListNode next;
          ListNode() {}
          ListNode(int val) { this.val = val; }
          ListNode(int val, ListNode next) { this.val = val; this.next = next; }
      }

    public static ListNode reverseKGroup(ListNode head, int k) {

        // dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        // connectorHeadToCurrentGroup = dummy (the node just BEFORE the group we are about to reverse)
        // Its job: after reversal, reconnect the previous part of list to the new head
        ListNode connectorHeadToCurrentGroup = dummy;

        while (true) {

            // For group [4,5,6]: currentGroupKthNode = 6  (last node of current group)
            ListNode currentGroupKthNode = getKthNode(connectorHeadToCurrentGroup, k);
            if (currentGroupKthNode == null)
                return dummy.next;

            // nextGroupFirstNode = 7  (first node of the group we will reverse NEXT iteration)
            ListNode nextGroupFirstNode = currentGroupKthNode.next;

            // oldGroupHead = 4  (first node of current group = will become TAIL after reversal)
            // We save it now because the for-loop will move connectorHeadToCurrentGroup.next away
            ListNode oldGroupHead = connectorHeadToCurrentGroup.next;

            // ── Reverse [4, 5, 6] ──────────────────────────────────────────
            //
            // prev starts at 7 (nextGroupFirstNode) so that after reversal,
            // the tail node (4) automatically points to the next group
            //
            //   Before loop:  prev=7   curr=4
            //
            //   x=0:  4.next=7,  prev=4,  curr=5   →  4->7
            //   x=1:  5.next=4,  prev=5,  curr=6   →  5->4->7
            //   x=2:  6.next=5,  prev=6,  curr=7   →  6->5->4->7
            //
            //   After loop: reversed chain = 6->5->4->7  ✓
            ListNode prev = nextGroupFirstNode;
            ListNode curr = connectorHeadToCurrentGroup.next; // = 4

            for (int x = 0; x < k; x++) {
                ListNode temp = curr.next; // save next before we overwrite curr.next
                curr.next = prev;          // point current node backward
                prev = curr;               // prev advances to current
                curr = temp;               // curr advances to next
            }

            // ── Reconnect reversed group into the main list ─────────────────
            //
            // State right now:
            //   connectorHeadToCurrentGroup = node(3)  [previous group's tail]
            //   currentGroupKthNode         = node(6)  [new HEAD of reversed group]
            //   oldGroupHead                = node(4)  [new TAIL of reversed group]
            //   node(4).next                = node(7)  [already wired inside loop]
            //
            //   List so far: dummy->1->2->3  |  6->5->4->7->8->9
            //                                ^
            //                         gap here — need to bridge it

            // Bridge the gap: 3.next = 6
            // Now: dummy->1->2->3->6->5->4->7->8->9  ✓
            connectorHeadToCurrentGroup.next = currentGroupKthNode;

            // Advance connectorHeadToCurrentGroup to node(4)
            // node(4) is the tail of the group we just reversed
            // In the NEXT iteration, we need to attach the next reversed group
            // to node(4).next — so connectorHeadToCurrentGroup must sit at node(4)
            //
            // After this line:
            //   connectorHeadToCurrentGroup = node(4)
            //   Ready for next group [7,8,9]
            connectorHeadToCurrentGroup = oldGroupHead;
        }
    }

    public static ListNode reverseKGroup1(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode connectorHeadToCurrentGroup = dummy;

        while (true) {
            ListNode currentGroupKthNode = getKthNode(connectorHeadToCurrentGroup, k);
            if (currentGroupKthNode == null)
                return dummy.next;

            ListNode nextGroupFirstNode = currentGroupKthNode.next;
            ListNode oldGroupHead = connectorHeadToCurrentGroup.next;

            ListNode prev = nextGroupFirstNode;
            ListNode curr = connectorHeadToCurrentGroup.next;

            for (int x = 0; x < k; x++) {
                ListNode temp = curr.next;
                curr.next = prev;
                prev = curr;
                curr = temp;
            }
            connectorHeadToCurrentGroup.next = currentGroupKthNode;
            connectorHeadToCurrentGroup = oldGroupHead;
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
            ListNode head1 = reverseKGroup1(head , 3);
            System.out.println(" ");
            printList(head1);
        }
}
