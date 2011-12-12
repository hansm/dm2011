import java.util.ArrayList;

import org.omg.CORBA.FREE_MEM;

import net.sf.javaml.core.kdtree.KDTree;

public class DCBOR implements ClusteringAlgorithm {
	private double ratio;
	private ArrayList<DataPoint> points;
	private PointDensity[] dpoints;
	private KDTree kdtree;
	private int k = 10;

	public DCBOR(ArrayList<DataPoint> points, double ratio) {
		this.ratio = ratio;
		this.points = points;

		fillKDtree();
	}

	private void fillKDtree() {
		kdtree = new KDTree(2);
		double[] coords = new double[2];
		for (int i = 0; i < points.size(); i++) {
			coords[0] = points.get(i).getX();
			coords[1] = points.get(i).getY();

			kdtree.insert(coords, i);
		}
	}

	private void displayDensityHistogram() {
		double[][] ranges = new double[20][2];
		int[] count = new int[20];
		ranges[0][0] = 0.0;
		ranges[0][1] = 0.05;
		double cratio = 0.0;

		for (int i = 1; i < ranges.length; i++) {
			ranges[i][0] = ranges[i - 1][1] + 0.01;
			ranges[i][1] = ranges[i - 1][1] + 0.05;
		}

		for (int i = 0; i < dpoints.length; i++) {
			cratio = dpoints[i].density / dpoints[dpoints.length - 1].density;
			System.out.println("cratio: " + cratio);
			for (int j = 0; j < ranges.length; j++) {
				if (cratio >= ranges[j][0] && cratio <= ranges[j][1]){
					count[j]++;
					break;
				}
			}
		}

		for (int i = 0; i < ranges.length / 2; i++) {
			System.out.printf("%.2f - %.2f: %d points\n", ranges[i][0],
					ranges[i][1], count[i]);
			dbc.freqtable = dbc.freqtable
					+ String.format(
							"%.2f - %.2f: %d points\t%.2f - %.2f: %d points\n",
							ranges[i][0], ranges[i][1], count[i + 10],
							ranges[i + 10][0], ranges[i + 10][1], count[i]);
		}
	}

	private void createDensityList() {
		dpoints = new PointDensity[points.size()];
		for (int i = 0; i < points.size(); i++)
			dpoints[i] = new PointDensity();
		double[] coords = new double[2];
		for (int i = 0; i < points.size(); i++) {
			coords[0] = points.get(i).getX();
			coords[1] = points.get(i).getY();

			dpoints[i].setDatapoint(points.get(i));
			dpoints[i].insertNeighbors(kdtree.nearest(coords, k + 1));
		}

		java.util.Arrays.sort(dpoints);
		displayDensityHistogram();
	}

	public int run2() throws AlgorithmException {
		createDensityList();
		int numClusters = 0;
		double threshold = 0.0;
		ArrayList<PointDensity> seedlist;
		ArrayList<PointDensity> resultlist;

		// Remove the outliers, mark as noise = 0
		// Find the threshold
		// and initialize the points to unclustered = -1
		for (int i = 0; i < dpoints.length; i++) {
			if (dpoints[i].density / dpoints[dpoints.length - 1].density > ratio)
				dpoints[i].datapoint.cluster = 0;
			else
				dpoints[i].datapoint.cluster = -1;

			if (dpoints[i].neighbors[0].distance > threshold)
				threshold = dpoints[i].neighbors[0].distance;
		}

		PointDensity p;
		for (int i = 0; i < dpoints.length; i++) {
			if (dpoints[i].datapoint.cluster != -1)
				continue;

			numClusters++;

			dpoints[i].datapoint.cluster = numClusters;
			seedlist = appendPointsUnderThreshold(dpoints[i], threshold);
			if (!seedlist.isEmpty()) {
				for (int j = 0; j < seedlist.size(); j++)
					seedlist.get(j).datapoint.cluster = numClusters;

				while (!seedlist.isEmpty()) {
					p = seedlist.remove(0);
					resultlist = appendPointsUnderThreshold(p, threshold);
					if (!resultlist.isEmpty()) {
						for (int j = 0; j < resultlist.size(); j++)
							if (resultlist.get(j).datapoint.cluster == -1) {
								seedlist.add(resultlist.get(j));
								resultlist.get(j).datapoint.cluster = numClusters;
							}
					}
				}

			}
		}

		return numClusters;
	}

	public int run() throws AlgorithmException {
		createDensityList();
		int numClusters = 0;
		double threshold = 0.0;
		ArrayList<PointDensity> seedlist = new ArrayList<PointDensity>();

		// Remove the outliers, mark as noise = 0
		// Find the threshold
		// and initialize the points to unclustered = -1
		for (int i = 0; i < dpoints.length; i++) {
			System.out.println("Density: " + dpoints[i].density
					/ dpoints[dpoints.length - 1].density);
			if (dpoints[i].density / dpoints[dpoints.length - 1].density > ratio)
				dpoints[i].datapoint.cluster = 0;
			else {
				dpoints[i].datapoint.cluster = -1;

				if (dpoints[i].neighbors[0].distance > threshold)
					threshold = dpoints[i].neighbors[0].distance;
			}
		}
		System.out.println("Threshold: " + threshold);

		PointDensity p;
		for (int i = 0; i < dpoints.length; i++) {
			if (dpoints[i].datapoint.cluster != -1)
				continue;

			numClusters++;

			seedlist.add(dpoints[i]);
			while (!seedlist.isEmpty()) {
				p = seedlist.remove(0);
				p.datapoint.cluster = numClusters;
				for (int j = 0; j < p.neighbors.length; j++)
					if (p.neighbors[j].p.datapoint.cluster == -1
							&& p.neighbors[j].distance < threshold)
						seedlist.add(p.neighbors[j].p);
			}
		}

		return numClusters;
	}

	private ArrayList<PointDensity> appendPointsUnderThreshold(PointDensity p,
			double threshold) {
		ArrayList<PointDensity> arr = new ArrayList<PointDensity>();
		for (int i = 0; i < p.neighbors.length; i++) {
			if (p.neighbors[i].distance < threshold)
				arr.add(p.neighbors[i].p);
		}
		return arr;
	}

	private class PointDensity implements Comparable<PointDensity> {
		private double density = 0;
		private DataPoint datapoint = null;
		private PointDistance[] neighbors;

		public void insertNeighbors(Object[] neigh) {
			neighbors = new PointDistance[neigh.length - 1];
			for (int i = 0; i < neighbors.length; i++) {
				neighbors[i] = new PointDistance();
				neighbors[i].distance = datapoint.calcDistance(points
						.get((Integer) neigh[i + 1]));
				neighbors[i].p = dpoints[(Integer) neigh[i + 1]];
				density += neighbors[i].distance;
			}
			java.util.Arrays.sort(neighbors);
		}

		public void setDatapoint(DataPoint datapoint) {
			this.datapoint = datapoint;
		}

		@Override
		public int compareTo(PointDensity o) {
			if (density < o.density)
				return -1;
			if (density > o.density)
				return 1;
			return 0;
		}
	}

	private class PointDistance implements Comparable<PointDistance> {
		private PointDensity p;
		private double distance = 0;

		@Override
		public int compareTo(PointDistance o) {
			if (distance < o.distance)
				return -1;
			if (distance > o.distance)
				return 1;

			return 0;
		}
	}
}
