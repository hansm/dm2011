import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class SNNTest {

	@Test
	public void testRun() {
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		DataPoint p1 = new DataPoint(1, 1);
		points.add(p1);
		DataPoint p2 = new DataPoint(1, 2);
		points.add(p2);
		DataPoint p3 = new DataPoint(2, 1);
		points.add(p3);
		DataPoint p4 = new DataPoint(2, 2);
		points.add(p4);
		DataPoint p5 = new DataPoint(3, 1);
		points.add(p5);
		DataPoint p6 = new DataPoint(3, 2);
		points.add(p6);
		
		DataPoint p7 = new DataPoint(5, 5);
		points.add(p7);

		SNN algorithm = new SNN(points, 4, 2, 0, 2);
		try {
			algorithm.run();
		} catch (Exception e) {
			fail("Exception thrown.");
		}
		
		assertEquals("Point 1 ("+ p1.toString() +")", 1, p1.getCluster());
		assertEquals("Point 2 ("+ p2.toString() +")", 1, p2.getCluster());
		assertEquals("Point 3 ("+ p3.toString() +")", 1, p3.getCluster());
		assertEquals("Point 4 ("+ p4.toString() +")", 1, p4.getCluster());
		assertEquals("Point 5 ("+ p5.toString() +")", 1, p5.getCluster());
		assertEquals("Point 6 ("+ p6.toString() +")", 1, p6.getCluster());
		assertEquals("Point 7 ("+ p7.toString() +")", 0, p7.getCluster());
	}
	
	@Test
	public void testRun2() {
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		DataPoint p1 = new DataPoint(1, 1);
		points.add(p1);
		DataPoint p2 = new DataPoint(1, 2);
		points.add(p2);
		DataPoint p3 = new DataPoint(2, 1);
		points.add(p3);
		DataPoint p4 = new DataPoint(2, 2);
		points.add(p4);
		DataPoint p5 = new DataPoint(3, 1);
		points.add(p5);
		DataPoint p6 = new DataPoint(3, 2);
		points.add(p6);
		
		DataPoint p7 = new DataPoint(10, 5);
		points.add(p7);
		DataPoint p8 = new DataPoint(15, 6);
		points.add(p8);

		SNN algorithm = new SNN(points, 4, 2, 0, 2);
		try {
			algorithm.run();
		} catch (Exception e) {
			fail("Exception thrown.");
		}
		
		assertNotSame("Point 1 not noise.", 0, p1.getCluster());
		assertNotSame("Point 7 not in noise.", 0, p7.getCluster());
		assertNotSame("Points 1 and 7 not in same cluster.", p1.getCluster(), p7.getCluster());
		assertEquals("Points 1 and 2 share cluster.", p1.getCluster(), p2.getCluster());
		assertEquals("Points 1 and 3 share cluster.", p1.getCluster(), p3.getCluster());
		assertEquals("Points 1 and 4 share cluster.", p1.getCluster(), p4.getCluster());
		assertEquals("Points 1 and 5 share cluster.", p1.getCluster(), p5.getCluster());
		assertEquals("Points 1 and 6 share cluster.", p1.getCluster(), p6.getCluster());
		assertEquals("Point 7 ("+ p7.toString() +") and point 8 ("+ p8.toString() +") share cluster.", p8.getCluster(), p7.getCluster());
	}
	
	@Test(expected = AlgorithmException.class)
	public void testRunTooFewPoints() throws AlgorithmException {
		ArrayList<DataPoint> points = new ArrayList<DataPoint>();
		points.add(new DataPoint(1, 2));

		SNN algorithm = new SNN(points, 2, 2, 2, 2);
		algorithm.run();
	}

}
