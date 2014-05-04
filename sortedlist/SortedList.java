/**
 * EE382C Verification and Validation - Problem Set 1
 * @author Puneet Bansal
 */
package pset1;

import java.util.HashSet;
import java.util.Set;

public class SortedList {
	Node header;
	int size;

	static class Node {
		int elem;
		Node next;
		
		public Node(int elem) {
			this.elem = elem;
		}
		
		public Node getNext() {
			return next;
		}
		
		public int getElement() {
			return elem;
		}
		
		public void setNext(Node nextNode) {
			this.next = nextNode;
		}
		
		@Override
		public String toString() {
			return String.valueOf(getElement());
		}
		
	}

	public SortedList() {
		header = null;
		size = 0;
	}
	
	public boolean repOk() {
		// Returns true if and only if "this" satisfies the class invariant
		
		System.out.println("Validating list " + this);
		//Check that there are no cycles
		Set<Node> visitedNodes = new HashSet<Node>();
		
		Node currentNode = getHeader();
		while (currentNode != null) {
			boolean added = visitedNodes.add(currentNode);
			if (!added) {
				System.out.println("There are cycles in the list!");
				return false;
			}
			currentNode = currentNode.getNext();
		}
		
		//Check that size field matches the actual number of nodes in the list
		if (visitedNodes.size() != getSize()) {
			System.out.println("Size " + getSize() + " does not match the actual size " + visitedNodes.size());
			return false;
		}
		
		//Check that the list is sorted in ascending order
		currentNode = getHeader();
		while (currentNode != null && currentNode.getNext() != null) {
			boolean isGreater = currentNode.getElement() > currentNode.getNext().getElement();
			if (isGreater) {
				System.out.println("List is not sorted!" + currentNode.getElement() + " is greater than its successor " + currentNode.getNext().getElement());
				return false;
			}
			currentNode = currentNode.getNext();
		}
		
		//Check that list has no repetitions
		currentNode = getHeader();
		Set<Integer> nodeData = new HashSet<Integer>();
		
		while (currentNode != null) {
			boolean added = nodeData.add(currentNode.getElement());
			if (!added) {
				System.out.println("There are repitions in the list. " + currentNode.getElement() + " is repeated");
				return false;
			}
			currentNode = currentNode.getNext();
		}
		return true;
	}
	
	public boolean contains(int x) {
		//Returns true if and only if x is an element contained in "this"
		Node currentNode = getHeader();
		while (currentNode != null) {
			if (currentNode.getElement() == x) {
				return true;
			}
			currentNode = currentNode.getNext();
		}
		return false;
	}
	
	
	public void add(int x) {
		// adds given element x to "this" subject to the class invariant
		if (contains(x)) {
			System.out.println("Warning: " + x + " is already present in the list.");
			return;
		} else {
			System.out.println("Adding " + x);
		}
		
		Node newNode = new Node(x);
		
		//Empty list
		if (getSize() == 0) {
			setHeader(newNode);
			size = size + 1;
			return;
		}
		
		//Check if element is to be added at the beginning of the list
		Node currentNode = getHeader();
		if (x < getHeader().getElement()) {
			Node oldHeader = getHeader();
			setHeader(newNode);
			newNode.setNext(oldHeader);
			size = size + 1;
			return;
		}

		//Check if element is to be added at the end of the list
		while (currentNode != null) {
			if (currentNode.getNext() == null && x > currentNode.getElement()) {
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
			if (currentNode != null && x > currentNode.getElement() && x < currentNode.getNext().getElement()) {
				newNode.setNext(currentNode.getNext());
				currentNode.setNext(newNode);
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
		if (x == getHeader().getElement()) {
			setHeader(getHeader().getNext());
			size = size - 1;
			return;
		}
		
		//Check if x is the last node
		Node currentNode = getHeader();
		while (currentNode.getNext() != null) {
			if (currentNode.getNext().getNext() == null && currentNode.getNext().getElement() == x) {
				currentNode.setNext(null);
				size = size - 1;
				return;
			}
			currentNode = currentNode.getNext();
		}
		
		//Element is in between two nodes
		currentNode = getHeader();
		while (true) {
			if (currentNode != null && x == currentNode.getNext().getElement()) {
				System.out.println("Found " + x);
				System.out.println("Linking " + currentNode + " to " + currentNode.getNext().getNext());
				currentNode.setNext(currentNode.getNext().getNext());
				size = size - 1;
				return;
			}
			currentNode = currentNode.getNext();
		}
	}
	
	public int getSize() {
		return this.size;
	}
	
	public Node getHeader() {
		return this.header;
	}
	
	private void setHeader(Node headerNode) {
		this.header = headerNode;
	}
	
	@Override
	public String toString() {
		Node currentNode = getHeader();
		String listStr = "[";
		while (currentNode != null) {
			if (currentNode != getHeader()) {
				listStr = listStr + " --> ";
			}
			listStr = listStr + currentNode;
			currentNode = currentNode.getNext();
		}
		return listStr + "]";
	}
}
