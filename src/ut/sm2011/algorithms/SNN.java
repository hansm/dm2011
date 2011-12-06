package ut.sm2011.algorithms;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

/**
 * Shared Nearest Neighbor (SNN) implementation.
 */
public class SNN implements ClusteringAlgorithm {

	private int K;

	private int coreThreshold;

	private int noiseThreshold;

	private int linkThreshold;

	private ArrayList<DataPoint> points;

	/**
	 * SNN clustering
	 * 
	 * @param points
	 *            ArrayList of points to cluster
	 * @param K
	 *            number of most similar neighbors to compare
	 * @param coreThreshold
	 *            density threshold for core points
	 * @param noiseThreshold
	 *            density threshold for noise points
	 * @param linkThreshold
	 *            similarity threshold for same cluster
	 */
	public SNN(ArrayList<DataPoint> points, int K, int coreThreshold,
			int noiseThreshold, int linkThreshold) {
		this.points = points;
		this.K = K;
		this.coreThreshold = coreThreshold;
		this.noiseThreshold = noiseThreshold;
		this.linkThreshold = linkThreshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ut.sm2011.algorithms.ClusteringAlgorithm#run()
	 */
	@Override
	public int run() throws AlgorithmException {
		if (points == null || points.size() == 0) {
			throw new AlgorithmException("Nothing to cluster.");
		}

		if (points.size() < K + 1) {
			throw new AlgorithmException(
					"You need at least K + 1 points to cluster.");
		}

		double[][] matrix = computeSimilarityMatrix();

		matrix = getKNearest(matrix);

		int[][] similarityMatrix = createSNNMatrix(matrix);

		findCoreAndNoise(similarityMatrix);

		removeUnimportantLinks(similarityMatrix);

		return formClusters(similarityMatrix);
	}

	/**
	 * Compute similarity matrix
	 * 
	 * @return
	 */
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

	/**
	 * Sparsify the similarity matrix by keeping only the k most similar
	 * neighbors
	 * 
	 * @param matrix
	 *            similarity matrix
	 * @return matrix with only K nearest
	 */
	private double[][] getKNearest(double[][] matrix) {
		double[] rowCopy;
		double kVal;
		for (int i = 0; i < matrix.length; i++) {
			// find K-th nearest point
			rowCopy = matrix[i].clone();
			Arrays.sort(rowCopy);
			kVal = rowCopy[K];

			// remove all other connections
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] > kVal) {
					matrix[i][j] = 0;
				}
			}
		}
		return matrix;
	}

	/**
	 * Construct the shared nearest neighbor graph from the sparsified
	 * similarity matrix. Graph is in matrix format.
	 * 
	 * @param matrix
	 *            K-closest matrix
	 * @return
	 */
	private int[][] createSNNMatrix(double[][] matrix) {
		int[][] m = new int[matrix.length][matrix.length];

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				m[i][j] = 0;
			}
		}

		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				if (i == j) {
					continue;
				}

				for (int k = 0; k < m.length; k++) {
					if (matrix[i][k] > 0 && matrix[j][k] > 0) {
						m[i][j]++;
					}
				}
			}
		}

		return m;
	}

	/**
	 * For every data point in the graph, calculate the total strength of links
	 * coming out of the point.
	 * 
	 * @param matrix
	 * @return
	 */
	private int[] calcLinksTotal(int[][] matrix) {
		int[] links = new int[matrix.length];
		for (int i = 0; i < links.length; i++) {
			links[i] = 0;
			for (int j = 0; j < links.length; j++) {
				links[i] += matrix[i][j];
			}
		}
		return links;
	}

	/**
	 * Identify representative points by choosing the points that have high
	 * density. Identify noise points by choosing the points that have low
	 * density and remove them (into cluster 0, cluster for noise points).
	 * 
	 * @param similarityMatrix
	 */
	private void findCoreAndNoise(int[][] similarityMatrix) {
		int[] linksTotal = calcLinksTotal(similarityMatrix);

		for (int i = 0; i < linksTotal.length; i++) {
			// check if is core
			points.get(i).setCore(linksTotal[i] >= coreThreshold);

			// check if is noise
			points.get(i).setCluster(linksTotal[i] < noiseThreshold ? 0 : -1);
		}
	}

	/**
	 * Remove all links between points that have weight smaller than a threshold
	 * 
	 * @param similarityMatrix
	 */
	private void removeUnimportantLinks(int[][] similarityMatrix) {
		for (int i = 0; i < similarityMatrix.length; i++) {
			for (int j = 0; j < similarityMatrix.length; j++) {
				if (similarityMatrix[i][j] < linkThreshold) {
					similarityMatrix[i][j] = 0;
				}
			}
		}
	}

	/**
	 * Take connected components of points to form clusters, where every point
	 * is a cluster is either a representative point (core) or is connected to a
	 * representative point.
	 * 
	 * @param similarityMatrix
	 */
	private int formClusters(int[][] similarityMatrix) {
		int cluster = 0;
		int j;
		Queue<Integer> queue = new LinkedList<Integer>();

		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).getCluster() != -1 || !points.get(i).isCore()) {
				continue;
			}

			// next cluster
			cluster++;

			// BFS to connect core points into clusters
			queue.add(i);
			while (!queue.isEmpty()) {
				j = (int) queue.poll();
				
				// set cluster
				points.get(j).setCluster(cluster);

				for (int k = 0; k < points.size(); k++) {
					if (similarityMatrix[j][k] > 0
							&& points.get(k).getCluster() == -1
							&& points.get(k).isCore()) {
						queue.add(k);
					}
				}
			}
		}

		// connect all border points to nearest core point
		DataPoint connectTo;
		int connectToSimilarity;
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).getCluster() == -1 && !points.get(i).isCore()) {
				connectTo = null;
				connectToSimilarity = 0;
				for (j = 0; j < points.size(); j++) {
					if (similarityMatrix[i][j] > 0 && points.get(j).isCore() && (connectTo == null || connectToSimilarity < similarityMatrix[i][j])) {
						connectTo = points.get(j);
						connectToSimilarity = similarityMatrix[i][j];
					}
				}
				
				points.get(i).setCluster(connectTo == null ? 0 : connectTo.getCluster());
			}
		}

		return cluster;
	}
}
