/**
 * @author Puneet Bansal
 */


import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.vandv.SortedList;
import com.vandv.TestDataReader;

@RunWith(Parameterized.class)
public class SortedListTest {
	
	SortedList sl;
	
	public SortedListTest(SortedList pbList) {
		this.sl = pbList;
	}

	@Parameters
	public static Collection<SortedList[]> getData() {
		List<SortedList[]> list = TestDataReader.getData();
		return  list;
		
	}
	
	@Test
	public void testAddDoesNotAddDuplicate() {
		
		System.out.println("Running test on list " + getListAsString());
		
		int numToAdd = 5;
		
		sl.add(numToAdd);
		
		int elementFoundCount = 0;
		
		SortedList.Node currentNode = sl.getHeader();
		while (currentNode != null) {
			if (currentNode.getElem() == numToAdd) {
				elementFoundCount++;
			}
			currentNode = currentNode.getNext();
		}			
		Assert.assertEquals(elementFoundCount,1);
	}
	
	private String getListAsString() {
		
		StringBuilder listStr = new StringBuilder("[");
		
		if (sl != null) {
			SortedList.Node currentNode = sl.getHeader();
				while (currentNode != null) {
					listStr.append(currentNode.getElem()).append(",");
					currentNode = currentNode.getNext();
				}
		   if (listStr.length() > 1 && listStr.charAt(listStr.length() - 1) == ',') {
			   listStr.deleteCharAt(listStr.length() - 1);
			   listStr.append("]");
		   }
		}
	 return listStr.toString();	
	}
}
