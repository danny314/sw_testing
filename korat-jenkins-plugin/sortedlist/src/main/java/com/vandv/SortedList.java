package com.vandv;

/**
 * @author Puneet Bansal
 */
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import korat.finitization.IFinitization;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;

public class SortedList implements Serializable {
	
	private static final long serialVersionUID = 1498989006892870224L;
	
	private Node header;
	private int size;

	public static class Node implements Serializable {

		private static final long serialVersionUID = 1283803738099414890L;
		
		int elem;
		Node next;
		
		public Node(int elem) {
			this.elem = elem;
		}
		
		public Node getNext() {
			return next;
		}
		
		public int getElem() {
			return elem;
		}
		
		public void setElem(int elem) {
			this.elem = elem;
		}

		public void setNext(Node nextNode) {
			this.next = nextNode;
		}
		
		@Override
		public String toString() {
			return String.valueOf(getElem());
		}
		
	}

	public SortedList() {
		header = null;
		size = 0;
	}
	
	public boolean repOK() {
		// Returns true if and only if "this" satisfies the class invariant
		
		System.out.println("Validating list " + this);
		
		//Check that there are no cycles
		Set<Node> visitedNodes = new HashSet<Node>();
		
		Node currentNode = getHeader();
		while (currentNode != null) {
			boolean added = visitedNodes.add(currentNode);
			if (!added) {
				System.out.println("There are cycles in the list!");
				System.out.println("Returning false...");
				return false;
			}
			currentNode = currentNode.getNext();
		}

		
		//Check that size field matches the actual number of nodes in the list
		if (visitedNodes.size() != getSize()) {
			System.out.println("Size " + getSize() + " does not match the actual size " + visitedNodes.size());
			System.out.println("Returning false...");
			return false;
		}
		
		//Check that the list is sorted in ascending order
		currentNode = getHeader();
		while (currentNode != null && currentNode.getNext() != null) {
			boolean isGreater = currentNode.getElem() > currentNode.getNext().getElem();
			if (isGreater) {
				System.out.println("List is not sorted!" + currentNode.getElem() + " is greater than its successor " + currentNode.getNext().getElem());
				System.out.println("Returning false...");
				return false;
			}
			currentNode = currentNode.getNext();
		}
		
		//Check that list has no repetitions
		System.out.println("Checking list has no repitions");
		currentNode = getHeader();
		Set<Integer> nodeData = new HashSet<Integer>();
		
		while (currentNode != null) {
			boolean added = nodeData.add(currentNode.getElem());
			if (!added) {
				System.out.println("There are repitions in the list. " + currentNode.getElem() + " is repeated");
				System.out.println("Returning false...");
				return false;
			}
			currentNode = currentNode.getNext();
		}
		
		System.out.println("Returning true...");
		//Change 38
		return true;
		
	}
	
		 public static IFinitization finSortedList(int size) {
	        return finSortedList(size, size, size);
		  }

	    public static IFinitization finSortedList(int nodesNum, int minSize, int maxSize) {
	        IFinitization f = FinitizationFactory.create(SortedList.class);
	        IObjSet nodes = f.createObjSet(Node.class, nodesNum, true);
	        f.set("header", nodes);
	        f.set("size", f.createIntSet(minSize, maxSize));
	        f.set("Node.next", nodes);
	        f.set("Node.elem", f.createIntSet(1, maxSize));
	        return f;
	    }
	    
		public boolean contains(int x) {
			//Returns true if and only if x is an element contained in "this"
			Node currentNode = getHeader();
			while (currentNode != null) {
				if (currentNode.getElem() == x) {
					return true;
				}
				currentNode = currentNode.getNext();
			}
			return false;
		}

		public Node getHeader() {
			return header;
		}

		public void setHeader(Node header) {
			this.header = header;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}
	    
		public void add(int x) {
			// adds given element x to "this" subject to the class invariant
/*			
			if (contains(x)) {
				System.out.println("Warning: " + x + " is already present in the list.");
				return;
			} else {
				System.out.println("Adding " + x);
			}
*/			
			Node newNode = new Node(x);
			
			//Empty list
			if (getSize() == 0) {
				setHeader(newNode);
				size = size + 1;
				return;
			}
			
			//Check if element is to be added at the beginning of the list
			Node currentNode = getHeader();
			if (x < getHeader().getElem()) {
				Node oldHeader = getHeader();
				setHeader(newNode);
				newNode.setNext(oldHeader);
				size = size + 1;
				return;
			}

			//Check if element is to be added at the end of the list
			while (currentNode != null) {
				if (currentNode.getNext() == null && x > currentNode.getElem()) {
					currentNode.setNext(newNode);
					newNode.setNext(null);
					size = size + 1;
					return;
				}
				currentNode = currentNode.getNext();
			}
			
			//The element is to be added in between
			currentNode = getHeader();
			while (true) {
				if (currentNode != null && x > currentNode.getElem() && x < currentNode.getNext().getElem()) {
					newNode.setNext(currentNode.getNext());
					currentNode.setNext(newNode);
					size = size + 1;
					return;
				}
				if (currentNode != null) {
					currentNode = currentNode.getNext();
				} else {
					break;
				}
			}
			
			//Did not fall in any case. Add element to the last (intentional bug for demo)
			currentNode = getHeader();
			while (currentNode != null) {
				if (currentNode.getNext() == null) {
					currentNode.setNext(newNode);
					newNode.setNext(null);
					size = size + 1;
					return;
				}
				currentNode = currentNode.getNext();
			}			
		}

		public void remove(int x) {
			//removes the given element x from "this" subject to the class invariant
			if (!contains(x)) {
				System.out.println("Warning: The element " + x + " is not present in the list.");
				return;
			}
			//Check if x is the first node
			if (x == getHeader().getElem()) {
				setHeader(getHeader().getNext());
				size = size - 1;
				return;
			}
			
			//Check if x is the last node
			Node currentNode = getHeader();
			while (currentNode.getNext() != null) {
				if (currentNode.getNext().getNext() == null && currentNode.getNext().getElem() == x) {
					currentNode.setNext(null);
					size = size - 1;
					return;
				}
				currentNode = currentNode.getNext();
			}
			
			//Element is in between two nodes
			currentNode = getHeader();
			while (true) {
				if (currentNode != null && x == currentNode.getNext().getElem()) {
					System.out.println("Found " + x);
					System.out.println("Linking " + currentNode + " to " + currentNode.getNext().getNext());
					currentNode.setNext(currentNode.getNext().getNext());
					size = size - 1;
					return;
				}
				currentNode = currentNode.getNext();
			}
		}
}
