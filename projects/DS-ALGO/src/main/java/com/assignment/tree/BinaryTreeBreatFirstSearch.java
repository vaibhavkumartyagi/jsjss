package com.assignment.tree;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

class BinaryTreeBreatFirstSearch {
	

	Node root;
	BinaryTreeBreatFirstSearch() {
		root = null;
	}
	private static Queue<Node> queue = new LinkedList<>();
   
	void printTopView() {
		while (!queue.isEmpty()) {

			Node node = queue.remove();
			System.out.print(" " + node.key );
			
			if (node.left != null)
				queue.add(node.left);
			if (node.right != null)
				queue.add(node.right);
		}
	}
	void displayTopView() {
		queue.add(root);
		printTopView();
	}

	public static void main(String[] args) {
		BinaryTreeBreatFirstSearch tree = new BinaryTreeBreatFirstSearch();
		tree.root = new Node(1);
		tree.root.left = new Node(2);
		tree.root.right = new Node(3);
		tree.root.left.left = new Node(4);
		tree.root.left.right = new Node(5);
		tree.root.left.right.left = new Node(6);
		tree.root.right.right = new Node(7);
		tree.displayTopView();
	}
}