package ut.sm2011;

import java.util.Random;
import java.util.Vector;

import ut.sm2011.algorithms.*;

public class StartHere {

	public static void outMatrix(double[][] m) {
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				System.out.print(Math.round(m[i][j]) + "\t");
			}
			System.out.println();
		}
	}

	public static void outMatrix(int[][] m) {
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				System.out.print(m[i][j] + "\t");
			}
			System.out.println();
		}
	}
	
	public static void outPoints(Vector<DataPoint> points) {
		for (DataPoint i : points) {
			System.out.println(i.toString());
		}
	}
	
	
	public static void main(String[] args) {
		Random rand = new Random();
		
		Vector<DataPoint> points = new Vector<DataPoint>();
		for (int i = 0; i < 100; i++) {
			points.add(new DataPoint(rand.nextInt(100), rand.nextInt(100)));
		}
		
		ClusteringAlgorithm algorithm = new SNN(points, 20, 20, 0, 15);
		algorithm.run();
		
		outPoints(points);
	}
	
}
