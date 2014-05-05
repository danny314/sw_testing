package pset3;


import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/*
 * @author Puneet Bansal
 */
public class Path {
	Node first; // first == null iff path is empty
	
	static class Node {
		Node next;
		int id; // identifies the node

		Node(int n) {
			id = n;
		}

		public boolean equals(Object o) {
			if (o.getClass() != Node.class)
				return false;
			return id == ((Node) o).id;
		}

		public int hashCode() {
			return id;
		}
		
		public int getId() {
			return id;
		}

		public Node getNext() {
			return this.next;
		}
		
		public void setNext(Node nextNode) {
			this.next = nextNode;
		}
		
		@Override
		public String toString() {
			return "["+ getId() + "]";
		}
	}

	public Node getFirst() {
		return this.first;
	}
	
	public void setFirst(Node firstNode) {
		this.first = firstNode;
	}

	
	public void addNode(Node nextNode) {
		Node currentNode = getFirst();
		if (currentNode == null) {
			setFirst(nextNode);
			return;
		}
		while (true) {
			if (currentNode.getNext() == null) {
				currentNode.setNext(nextNode);
				break;
			}
			currentNode = currentNode.getNext();
		}
	}

	public void addNode(int nodeId) {
		Node newNode = new Node(nodeId);
		addNode(newNode);
	}

	public boolean isSimple() { // 5 points
		// postcondition: returns true iff "this" is a simple path
		Node currentNode = getFirst();

		Set<Node> visited = new HashSet<Node>();
		
		int nodeCount = 0;
		
		boolean isSimple = true;
		
		while (currentNode != null) {
			nodeCount = nodeCount + 1;
			boolean added = visited.add(currentNode);
			if (!added && !currentNode.equals(getFirst())) {
				isSimple = false;
				System.out.println("Path is not simple! Offending node = " + currentNode);
				break;
			}
			currentNode = currentNode.getNext();
		}
		
		return isSimple;
	}

	public boolean remainsSimpleAfterEdgeAddition(int from, int to) { // 5
																		// points
		// precondition: "this.isSimple()" and either "this" is empty or the
		// last node in "this" has "id" field with value "from"
		// postcondition: returns true iff addition of edge "from"->"to" at
		// the end of "this" would result in a simple path;
		
		if (!isSimple()) throw new IllegalArgumentException("Precondition failed : " + this + " is not simple");
		
		Node currentNode = getFirst();
		
		while (currentNode != null && currentNode.getNext() != null) {
			currentNode = currentNode.getNext();
		}
		
		if (getFirst() != null && currentNode.getId() != from) {
				throw new IllegalArgumentException("Precondition failed : " + this + " is not empty and " + currentNode.getNext() + " does not match " + from);
		}
		
		addNode(to);
		
		boolean isSimple = isSimple();
		//Undo the add
		currentNode = getFirst();
		if (currentNode.getNext() == null) {
			setFirst(null);
			return isSimple();
		} 
		while (true) {
			if (currentNode.getNext().getNext() == null) {
				currentNode.setNext(null);
				break;
			}
			currentNode = currentNode.getNext();
		}
		return isSimple;
	}

	public boolean isProperSubpath(Path p) { // 10 points
		// precondition: p != null
		// postcondition: returns true iff this is a proper subpath of p
		if (p == null) throw new IllegalArgumentException("Precondition failed : path is null");
		
		boolean isSubPath = false;
		boolean isProper = false;
		
		Node pCurrentNode = p.getFirst();
		Node currentNode = getFirst();
		int size = 0;
		
		outer:
		while (pCurrentNode != null && currentNode != null) {
			while (pCurrentNode != null && currentNode != null && pCurrentNode.equals(currentNode)) {
				//Found starting point - check the remaining path
				size = size + 1;
				currentNode = currentNode.getNext();
				pCurrentNode = pCurrentNode.getNext();
			}
			if (currentNode == null) {
				//We reached the end of the path we were checking, it means 'this' is subpath of p
				isSubPath = true;
				break outer;
				//Check if it is proper
			} else {
				//This streak did not match, start from beginning for the next streak.
				currentNode = getFirst();
				size = 0;
			}
			if (pCurrentNode != null) {
				pCurrentNode = pCurrentNode.getNext();
			}
		}
		
		if (isSubPath) {
			int pSize = 0;
			pCurrentNode = p.getFirst();
			
			while (pCurrentNode != null) {
				pSize = pSize + 1;
				pCurrentNode = pCurrentNode.getNext();
			}
			isProper = pSize != size;
			if (isProper) {
				System.out.println("'this' is proper subpath of p" );
			} else {
				System.out.println("'this' is subpath of p but is not proper" );
			}
		} else {
			System.out.println("'this' is NOT a subpath of p" );
		}
		
		return isSubPath && isProper;
	}
	
	public boolean repOk() {
		
		//Check that there are no cycles based on identity comparison (not equals)
		Map<Node,Node> topoMap = new IdentityHashMap<Node, Node>();
		
		Node currentNode = getFirst();
		
		while (currentNode != null) {
			if (!topoMap.containsKey(currentNode)) {
				topoMap.put(currentNode, null);
			} else {
				System.out.println("There are structural cycles in the path! Offending node =" + currentNode);
				return false;
			}
			currentNode = currentNode.getNext();
		}
		return true;
	}
	
	public boolean isSameAs(Path p) { 
		
		Node currentNode = getFirst();
		Node pCurrentNode = p.getFirst();
		
		while (currentNode != null) {
			if (pCurrentNode == null || !currentNode.equals(pCurrentNode)) {
				return false;
			}
			currentNode = currentNode.getNext();
			pCurrentNode = pCurrentNode.getNext();
		}
		return true;
	}
}
