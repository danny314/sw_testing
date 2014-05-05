package pset3;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pset3.Path.Node;

/*
 * @author Puneet Bansal
 */
public class PathTester {
	
	Path path;
	
	@Before
	public void initPath() {
		path = new Path();
	}
	
	@Test
	public void testRepOkDetectsStructuralCycles() {
		System.out.println("\n*********** Test that repOk detects structural cycles*************");
		path.addNode(1);
		Node node2 = new Node(2);
		path.addNode(node2);
		path.addNode(3);
		
		//Add the already added node again.
		path.addNode(node2);
		Assert.assertFalse(path.repOk());
	}
	
	@Test
	public void testRepOkAllowsDuplicateIds() {
		System.out.println("\n*************Test that repOk allows duplicate node ids as long as the objects are different*************");
		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		path.addNode(2);
		Assert.assertTrue(path.repOk());
	}
	
	@Test
	public void testPathRemainsSimpleAfterEdgeAddition() {
		System.out.println("\n*************Test that path remains simple after edge addition when 'from' matches the id of the last node*************");
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.remainsSimpleAfterEdgeAddition(4, 5));
		Assert.assertTrue(path.repOk());

		Path copy = new Path();
		copy.addNode(2);
		copy.addNode(3);
		copy.addNode(4);
		
		//Check that path was not modified
		Assert.assertTrue(path.isSameAs(copy));

	}

	@Test
	public void testPathDoesNotRemainSimpleAfterEdgeAddition() {
		System.out.println("\n*************Test that path does not remain simple after edge addition when edge addition forms internal loops*************");
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		Assert.assertTrue(path.repOk());
		Assert.assertFalse(path.remainsSimpleAfterEdgeAddition(4, 3));
		Assert.assertTrue(path.repOk());

		Path copy = new Path();
		copy.addNode(2);
		copy.addNode(3);
		copy.addNode(4);
		
		//Check that path was not modified
		Assert.assertTrue(path.isSameAs(copy));
	}
	
	@Test
	public void testPathRemainsSimpleAfterEdgeAdditionWhenEntirePathIsALoop() {
		System.out.println("\n*************Test that path remains simple after edge addition when the entire path after edge addition forms a loop*************");
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.remainsSimpleAfterEdgeAddition(4, 2));
		Assert.assertTrue(path.repOk());

		Path copy = new Path();
		copy.addNode(2);
		copy.addNode(3);
		copy.addNode(4);
		
		//Check that path was not modified
		Assert.assertTrue(path.isSameAs(copy));
	}

	@Test
	public void testEdgeAdditionOnEmptyPath() {
		System.out.println("\n*************Test that edge addition on empty path keeps the path simple *************");
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.remainsSimpleAfterEdgeAddition(3, 5));
		Assert.assertTrue(path.isSameAs(new Path()));
	}

	@Test
	public void testEdgeAdditionOnEmptyPathWhenFromToAreSame() {
		System.out.println("\n*************Test that edge addition on empty path keeps the path simple when from and to are same*************");
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.remainsSimpleAfterEdgeAddition(3, 3));
		Assert.assertTrue(path.isSameAs(new Path()));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEdgeAdditionNonSimplePath() {
		System.out.println("\n*************Test that edge addition throws error if path is not simple*************");

		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		path.addNode(3);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.remainsSimpleAfterEdgeAddition(3, 5));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testEdgeAdditionThrowsErrorWhenFromDifferentFromLastNode() {
		System.out.println("\n*************Test that edge addition throws error when 'from' is not the same as id of the last node*************");
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.remainsSimpleAfterEdgeAddition(3, 5));
	}

	@Test
	public void testProperSubPath() {
		System.out.println("\n*************Test proper sub path*************");

		path.addNode(3);
		path.addNode(4);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertTrue(path.isProperSubpath(superPath));
	}
	
	@Test
	public void testNotASubPath() {
		System.out.println("\n*************Test given path is not a sub path*************");

		path.addNode(3);
		path.addNode(5);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}
	
	@Test
	public void testNotASubPathWhenPathIsSplit() {
		System.out.println("\n*************Test not a sub path when path is split*************");

		path.addNode(3);
		path.addNode(4);
		path.addNode(6);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);
		superPath.addNode(6);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}
	
	@Test
	public void testNotASubPathWhenPartialMatch() {
		System.out.println("\n*************Test not a sub path when partial match*************");

		path.addNode(5);
		path.addNode(6);
		path.addNode(7);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);
		superPath.addNode(6);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}

	@Test
	public void testProperSubPathWhenOnlySubPathIsEmpty() {
		System.out.println("\n*************Test proper sub path when only sub path is empty *************");

		Path superPath = new Path();
		superPath.addNode(1);

		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}
	
	@Test
	public void testProperSubPathWhenBothPathsEmpty() {
		System.out.println("\n*************Test proper sub path when both paths empty*************");

		Path superPath = new Path();

		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testProperSubPathThrowsErrorOnNullPath() {
		System.out.println("\n*************Test proper sub path throws error on null path*************");

		path.addNode(3);
		path.addNode(4);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.isProperSubpath(null));
	}	
	@Test
	public void testProperSubPathWithMultipleOccurencesOfStartingNode() {
		System.out.println("\n*************Test proper sub path is identified when first node of sub path appears multiple times*************");

		path.addNode(3);
		path.addNode(4);
		path.addNode(8);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);

		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);
		
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(8);
		superPath.addNode(9);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertTrue(path.isProperSubpath(superPath));
	}
	
	@Test
	public void testPathIsSubPathButNotProper() {
		System.out.println("\n*************Test path is sub path but not proper*************");

		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		path.addNode(5);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);
		
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}		
	
	@Test
	public void testPathIsNotSubPath() {
		System.out.println("\n*************Test path is not sub path*************");

		path.addNode(3);
		path.addNode(8);
		
		Path superPath = new Path();
		superPath.addNode(1);
		superPath.addNode(2);
		superPath.addNode(3);
		superPath.addNode(4);
		superPath.addNode(5);

		Assert.assertTrue(path.repOk());
		Assert.assertTrue(superPath.repOk());
		Assert.assertFalse(path.isProperSubpath(superPath));
	}	
	
	@Test
	public void testPathIsNotSimpleWhenThereIsCycleInBetween() {
		System.out.println("\n*************Test path is not simple when there is cycle in between*************");
		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		path.addNode(5);
		path.addNode(2);
		Assert.assertTrue(path.repOk());
		Assert.assertFalse(path.isSimple());
	}	
	
	
	@Test
	public void testPathIsSimpleAllowsSameFirstLastNodes() {
		System.out.println("\n*************Test simple path allows the first and last node to be same*************");
		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		path.addNode(4);
		path.addNode(5);
		path.addNode(1);
		Assert.assertTrue(path.repOk());
		Assert.assertTrue(path.isSimple());
	}
	
	@Test
	public void testTwoPathsNotSameWhenBothNotEmpty() {
		System.out.println("\n*************Test two non empty paths that have different node id sequences are not same*************");
		
		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		Assert.assertTrue(path.repOk());
		
		Path secondPath = new Path();
		secondPath.addNode(1);
		secondPath.addNode(2);
		secondPath.addNode(4);
		
		Assert.assertTrue(secondPath.repOk());
		Assert.assertFalse(path.isSameAs(secondPath));
	}

	@Test
	public void testTwoPathsNotSameWhenOnlyOneIsEmpty() {
		System.out.println("\n*************Test two paths are are not same when only one is not empty*************");
		
		path.addNode(1);
		path.addNode(2);
		path.addNode(3);
		Assert.assertTrue(path.repOk());
		
		Path secondPath = new Path();
		
		Assert.assertTrue(secondPath.repOk());
		Assert.assertFalse(path.isSameAs(secondPath));
	}
}
