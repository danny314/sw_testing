/**
 * EE382C Verification and Validation - Problem Set 1
 * @author Puneet Bansal
 */
package pset1;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SortedListTester {
	
	//Random number generator
	private Random random = new Random();

	private SortedList sl;

	@Before
	public void init() {
		sl = new SortedList();
	}

	@After
	public void testRepOk() {
		Assert.assertTrue(sl.repOk());
	}
	
	//*************************************  'add' test cases **********************************************************
	@Test //Test path 1 2
	public void testAddDoesNotAddExistingElement() {
		System.out.println("\nExecuting testAddDoesNotAddExistingElement");
		int firstElement = getRandomInt(); 
		int secondElement = getRandomInt(); 
		sl.add(firstElement);
		sl.add(secondElement);
		sl.add(firstElement);
		Assert.assertEquals(2,sl.getSize());
		Assert.assertTrue(checkElementExists(firstElement));
		Assert.assertTrue(checkElementExists(secondElement));
	}
	
	@Test //Test path 1 3 4
	public void testAddToEmptyList() {
		System.out.println("\nExecuting testAddToEmptyList");
		int firstElement = getRandomInt(); 
		sl.add(firstElement);
		Assert.assertEquals(1,sl.getSize());
		Assert.assertTrue(checkElementExists(firstElement));
	}
	
	@Test //Test path 1 3 5 6
	public void testAddToBeginning() {
		System.out.println("\nExecuting testAddToBeginning");
		int firstElement = getRandomInt(); 
		int secondElement = firstElement - 1; 
		sl.add(firstElement);
		sl.add(secondElement);
		Assert.assertEquals(2,sl.getSize());
		Assert.assertEquals(secondElement, sl.getHeader().getElement());
	}

	@Test //Test path 1 3 5 7 8 9
	public void testAddToLast() {
		System.out.println("\nExecuting testAddToLast");
		int firstElement = getRandomInt(); 
		int secondElement = firstElement + 1; 
		sl.add(firstElement);
		sl.add(secondElement);
		Assert.assertEquals(2,sl.getSize());
		Assert.assertEquals(secondElement, sl.getHeader().getNext().getElement());
	}

	@Test //Test path 1 3 5 7 8 10 7 11 12 13 15 12 13 14
	public void testAddInBetween() {
		System.out.println("\nExecuting testAddInBetween");
		int firstElement = getRandomInt(); 
		int secondElement = firstElement + 5; 
		int thirdElement = firstElement + 15; 
		int fourthElement = firstElement + 10; 
		sl.add(firstElement);
		sl.add(secondElement);
		sl.add(thirdElement);
		sl.add(fourthElement);
		Assert.assertEquals(4,sl.getSize());
		Assert.assertEquals(fourthElement, sl.getHeader().getNext().getNext().getElement());
	}
	
	//*************************************  'remove' test cases **********************************************************
	@Test //Test path 1 2
	public void testNonExistentElementCannotBeRemoved() {
		System.out.println("\nExecuting testNonExistentElementCannotBeRemoved");
		int firstElement = getRandomInt(); 
		sl.add(firstElement);
		sl.add(firstElement + 1);
		Assert.assertTrue(sl.repOk());
		sl.remove(firstElement + 2);
		Assert.assertEquals(2, sl.getSize());
	}	
	
	@Test //Test path 1 3 4
	public void testRemoveFirstElement() {
		System.out.println("\nExecuting testRemoveFirstElement");
		int firstElement = getRandomInt();
		sl.add(firstElement);
		sl.add(firstElement + 1);
		Assert.assertEquals(2, sl.getSize());
		Assert.assertTrue(sl.repOk());
		sl.remove(firstElement);
		Assert.assertFalse(checkElementExists(firstElement));
		Assert.assertEquals(1, sl.getSize());
	}
	
	@Test  //Test path 1 3 5 6 7 8 
	public void testRemoveLastElement() {
		System.out.println("\nExecuting testRemoveLastElement");
		int firstElement = getRandomInt();
		int secondElement = firstElement + 1;
		int thirdElement = secondElement + 1;
		sl.add(firstElement);
		sl.add(secondElement);
		sl.add(thirdElement);
		Assert.assertTrue(sl.repOk());
		sl.remove(thirdElement);
		Assert.assertFalse(checkElementExists(thirdElement));
		Assert.assertEquals(2, sl.getSize());
	}	
	
	@Test //Test path 1 3 5 6 7 9 6 10 11 12 14 11 12 13
	public void testRemoveElementInBetween() {
		System.out.println("\nExecuting testRemoveElementInBetween");
		int firstElement = getRandomInt();
		int secondElement = firstElement + 1;
		int thirdElement = secondElement + 1;
		int fourthElement = thirdElement + 1;
		sl.add(firstElement);
		sl.add(secondElement);
		sl.add(thirdElement);
		sl.add(fourthElement);
		Assert.assertTrue(sl.repOk());
		sl.remove(thirdElement);
		Assert.assertFalse(checkElementExists(thirdElement));
		Assert.assertEquals(3, sl.getSize());
	}
	

	
	//*************************************  'contains' test cases **********************************************************
	@Test    //Test path 1 2 3 4
	public void testElementExists() {
		System.out.println("\nExecuting testElementExists");
		int firstElement = getRandomInt(); 
		sl.add(firstElement);
		sl.add(getRandomInt());
		Assert.assertTrue(sl.repOk());
		Assert.assertTrue(sl.contains(firstElement));
		Assert.assertTrue(checkElementExists(firstElement));
		Assert.assertEquals(2, sl.getSize());
	}
	
	@Test  //Test path 1 2 3 5 6
	public void testElementDoesNotExist() {
		System.out.println("\nExecuting testElementDoesNotExist");
		int firstElement = getRandomInt(); 
		sl.add(firstElement);
		Assert.assertTrue(sl.repOk());
		Assert.assertFalse(sl.contains(firstElement + 2));
		Assert.assertFalse(checkElementExists(firstElement + 2));
		Assert.assertEquals(1, sl.getSize());
	}	
	
	//*************************************  utility methods **********************************************************

	private int getRandomInt() {
		int num = random.nextInt();
		while (num > Integer.MAX_VALUE - 10 || num < Integer.MIN_VALUE + 10) {
			num = random.nextInt();
		}
		return num;
	}
	
	private boolean checkElementExists(int x) {
		SortedList.Node currentNode = sl.getHeader();
		while (currentNode != null) {
			if (currentNode.getElement() == x) return true;
			currentNode = currentNode.getNext();
		}
		return false;
	}
}
