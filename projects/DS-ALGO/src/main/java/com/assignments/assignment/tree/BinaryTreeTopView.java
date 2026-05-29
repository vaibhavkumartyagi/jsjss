package com.assignments.assignment.tree;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

class BinaryTreeTopView {
	// Root of Binary Tree
	class Pair {

		public Node node;
		public int height;
        public int hd;

        public Pair(Node node, int height) {
			this.node = node;
			this.height = height;
            this.hd= height;//claude code variable
		}

		public Node getNode() {
			return node;
		}

		@Override
		public String toString() {
			return "Pair [node=" + node + ", height=" + height + "]";
		}

		public void setNode(Node node) {
			this.node = node;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

	}

	Node root;

	BinaryTreeTopView() {
		root = null;
	}

	private static Map<Integer, Node> map = new TreeMap<Integer, Node>();//sorting order left to right
	private static Queue<Pair> queue = new LinkedList<>();
   
	void printTopView(Node node, int index) {
		while (!queue.isEmpty()) {

			Pair pair = queue.remove();
			System.out.println("KEY " + pair.getNode().key + " Height is " + pair.getHeight());

			if (!map.containsKey(pair.getHeight())) {
				map.put(pair.getHeight(), pair.getNode());
			}
			if (pair.getNode().left != null)
				queue.add(new Pair(pair.getNode().left, pair.getHeight() - 1));
			if (pair.getNode().right != null)
				queue.add(new Pair(pair.getNode().right, pair.getHeight() + 1));
		}

		for (Map.Entry<Integer, Node> e : map.entrySet()) {
			System.out.print(" " + e.getValue().key);
		}

	}

    void printBottomView(Node node, int index) {
        while (!queue.isEmpty()) {

            Pair pair = queue.remove();
            System.out.println("KEY " + pair.getNode().key + " Height is " + pair.getHeight());

           // if (!map.containsKey(pair.getHeight())) {
                map.put(pair.getHeight(), pair.getNode());
           // }
            if (pair.getNode().left != null)
                queue.add(new Pair(pair.getNode().left, pair.getHeight() - 1));
            if (pair.getNode().right != null)
                queue.add(new Pair(pair.getNode().right, pair.getHeight() + 1));
        }

        for (Map.Entry<Integer, Node> e : map.entrySet()) {
            System.out.print(" " + e.getValue().key);
        }

    }

	void printTopView() {
		queue.add(new Pair(root, 0));
		printTopView(root, 0);
	}

    void printBottomView() {
        queue.add(new Pair(root, 0));
        printBottomView(root, 0);
    }

    // ── Left View ────────────────────────────────────────────────
// First node seen at each depth level = leftmost visible node
    void printLeftView() {
        if (root == null) return;

        // key = depth level, value = first node seen at that level
        Map<Integer, Node> map = new TreeMap<>();
        Queue<Pair> queue = new LinkedList<>();

        queue.add(new Pair(root, 0)); // hd field reused here as depth level

        while (!queue.isEmpty()) {
            Pair pair = queue.remove();
            int depth = pair.hd;

            // Only first node at each depth wins — same as top view logic
            if (!map.containsKey(depth)) {
                map.put(depth, pair.node);
            }

            // Left child first — ensures leftmost node is processed first at each level
            if (pair.node.left != null)
                queue.add(new Pair(pair.node.left, depth + 1));
            if (pair.node.right != null)
                queue.add(new Pair(pair.node.right, depth + 1));
        }

        System.out.print("\n Left View: ");
        for (Map.Entry<Integer, Node> e : map.entrySet()) {
            System.out.print(e.getValue().key + " ");
        }
        System.out.println();
    }

    // ── Right View ───────────────────────────────────────────────
// Last node seen at each depth level = rightmost visible node
    void printRightView() {
        if (root == null) return;

        // key = depth level, value = last node seen at that level
        Map<Integer, Node> map = new TreeMap<>();
        Queue<Pair> queue = new LinkedList<>();

        queue.add(new Pair(root, 0));

        while (!queue.isEmpty()) {
            Pair pair = queue.remove();
            int depth = pair.hd;

            // Always overwrite — last node at each depth wins — same as bottom view logic
            map.put(depth, pair.node);

            // Left child first — ensures rightmost node overwrites last at each level
            if (pair.node.left != null)
                queue.add(new Pair(pair.node.left, depth + 1));
            if (pair.node.right != null)
                queue.add(new Pair(pair.node.right, depth + 1));
        }

        System.out.print("\n Right View: ");
        for (Map.Entry<Integer, Node> e : map.entrySet()) {
            System.out.print(e.getValue().key + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
		BinaryTreeTopView tree = new BinaryTreeTopView();
		tree.root = new Node(1);
		tree.root.left = new Node(2);
		tree.root.right = new Node(3);
		tree.root.left.left = new Node(4);
		tree.root.left.right = new Node(5);
		tree.root.left.right.left = new Node(6);
		tree.root.right.right = new Node(7);
		tree.printTopView();
        tree.printBottomView();
        tree.printLeftView();
        tree.printRightView();

	}
}