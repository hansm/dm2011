import java.util.ArrayList;

public class DBSCAN implements ClusteringAlgorithm {
	private int minPts;
	private double eps;
	private ArrayList<DataPoint> points;
	private double[][] distanceMatrix;

	public DBSCAN(ArrayList<DataPoint> points, int minPts, double eps) {
		this.minPts = minPts;
		this.eps = eps;
		this.points = points;
	}

	private double[][] computeSimilarityMatrix() {
		double[][] matrix = new double[points.size()][points.size()];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				if (i == j) {
					matrix[i][j] = 0;
				} else {
					matrix[i][j] = points.get(i).calcDistance(points.get(j));
				}
			}
		}
		return matrix;
	}

	private ArrayList<Integer> getNeighborIndexes(int p) {
		ArrayList<Integer> n = new ArrayList<Integer>();
		for (int i = 0; i < points.size(); i++)
			if (p != i && distanceMatrix[p][i] < eps)
				n.add(i);
		return n;
	}

	@Override
	public int run() throws AlgorithmException {
		distanceMatrix = computeSimilarityMatrix();

		// initialize all points as unvisited (cluster = -1)
		for (int i = 0; i < points.size(); i++)
			points.get(i).cluster = -1;
		int numClusters = 0;
		ArrayList<Integer> N;
		ArrayList<Integer> N_prim;
		int pindex;
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).cluster != -1)
				continue;

			// points.get(i).cluster = -1;
			N = getNeighborIndexes(i);
			if (N.size() < minPts)
				points.get(i).cluster = 0;
			else {
				points.get(i).cluster = ++numClusters;
				for (int k = 0; k < N.size(); k++) {
					pindex = N.get(k);
					if (points.get(pindex).cluster == -1) {
						// points.get(pindex).cluster = -1;
						N_prim = getNeighborIndexes(pindex);
						if (N_prim.size() >= minPts)
							for (int j = 0; j < N_prim.size(); j++)
								N.add(N_prim.get(j));
					}
					if (points.get(pindex).cluster < 1)
						points.get(pindex).cluster = numClusters;
				}
			}
		}

		return numClusters;
	}

}