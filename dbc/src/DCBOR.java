import java.util.ArrayList;

import net.sf.javaml.core.kdtree.KDTree;


public class DCBOR implements ClusteringAlgorithm {
	private double ratio;
	private ArrayList<DataPoint> points;
	private PointDensity[] dpoints;
	private KDTree kdtree;
	private int k = 10;
	
	public DCBOR(ArrayList<DataPoint> points, double ratio){
		this.ratio = ratio;
		this.points = points;
		
		fillKDtree();
	}
	
	private void fillKDtree(){
		kdtree = new KDTree(2);
		double[] coords = new double[2];
		for(int i = 0; i < points.size(); i++){
			coords[0] = points.get(i).getX();
			coords[1] = points.get(i).getY();
			
			kdtree.insert(coords, i);
		}
	}
	
	private void createDensityList(){
		dpoints = new PointDensity[points.size()];
		
		double[] coords = new double[2];
		for(int i = 0; i < points.size(); i++){
			dpoints[i] = new PointDensity();
			coords[0] = points.get(i).getX();
			coords[1] = points.get(i).getY();
			
			dpoints[i].setDatapoint(points.get(i));
			dpoints[i].insertNeighbors(kdtree.nearest(coords, k+1));
		}
		
		java.util.Arrays.sort(dpoints);
	}

	@Override
	public int run() throws AlgorithmException {
//		for(int i = 0; i< dpoints.length; i++){
//			System.out.println(dpoints[i].density);
//			for(int j = 0; j < dpoints[i].neighbors.length; j++){
//				if(dpoints[i].neighbors[j].p == dpoints[i])
//					System.out.print("fuck no");
//				System.out.println("   "+dpoints[i].neighbors[j].distance);
//			}
//		}
		createDensityList();
		
		//Remove the outliers, mark as noise = 0
		//and initialize the points to unclustered = -1
		for(int i = 0; i < dpoints.length; i++)
			if(dpoints[i].density / dpoints[dpoints.length-1].density > ratio)
				dpoints[i].datapoint.cluster = 0;
			else
				dpoints[i].datapoint.cluster = -1;
		
		int numClusters = 0;
		return 0;
	}
	private class PointDensity implements Comparable<PointDensity>{
		private double density = 0;
		private DataPoint datapoint = null;
		private PointDistance[] neighbors;
		
		public void insertNeighbors(Object[] neigh){
			neighbors = new PointDistance[neigh.length-1];
			for(int i = 0; i < neighbors.length; i++){
				neighbors[i] = new PointDistance();
				neighbors[i].distance = datapoint.calcDistance(points.get((Integer)neigh[i+1]));
				neighbors[i].p = dpoints[(Integer)neigh[i+1]];
				density += neighbors[i].distance;
			}
			
			java.util.Arrays.sort(neighbors);
		}
		
		public void setDatapoint(DataPoint datapoint){
			this.datapoint = datapoint;
		}

		@Override
		public int compareTo(PointDensity o) {
			if(density < o.density)
				return -1;
			if(density > o.density)
				return 1;
			return 0;
		}
	}
	
	private class PointDistance implements Comparable<PointDistance>{
		private PointDensity p;
		private double distance = 0;
		@Override
		public int compareTo(PointDistance o) {
			if(distance < o.distance)
				return -1;
			if(distance > o.distance)
				return 1;
			
			return 0;
		}
	}
}
