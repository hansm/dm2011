import graph.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

import net.sf.javaml.core.kdtree.KDTree;

/**
 * Shared Nearest Neighbor (SNN) implementation.
 */
public class SNN implements ClusteringAlgorithm {

	/**
	 * Number of closest neighbors to compare
	 */
	private int k;

	/**
	 * Links strength threshold for core point
	 */
	private int minPts;

	/**
	 * Link strength threshold
	 */
	private int eps;

	/**
	 * Points to compare
	 */
	private ArrayList<DataPoint> points;
	
	/**
	 * KD-tree for quick finding of neighbors
	 */
	private KDTree kdtree;
	
	/**
	 * Shared nearest neighborhood graph
	 */
	private UndirectedGraph SNNGraph;

	public SNN(ArrayList<DataPoint> points, int k, int minPts, int eps) {
		this.points = points;
		this.k = k;
		this.minPts = minPts;
		this.eps = eps;
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

		if (points.size() < k + 1) {
			throw new AlgorithmException(
					"You need at least K + 1 points to cluster.");
		}
		
		fillKDTree();

		createSNNGraph(getKNearest());

		findCorePoints();

		return formClusters();
	}
	
	/**
	 * Fill KD-tree for quick finding of K nearest neighbors
	 */
	private void fillKDTree() {
		kdtree = new KDTree(2);
		int size = points.size();
		double[] key = new double[2];
		for (int i = 0; i < size; i++) {
			key[0] = points.get(i).getX();
			key[1] = points.get(i).getY();
			kdtree.insert(key, i);
		}
	}

	/**
	 * Find K most similar neighbors of each point
	 * 
	 * @return matrix with only K nearest
	 */
	private int[][] getKNearest() {
		int[][] kNearest = new int[points.size()][k];
		double[] key = new double[2];
		Object[] nearest;
		for (int i  = 0; i < points.size(); i++) {
			key[0] = points.get(i).getX();
			key[1] = points.get(i).getY();

			// get K + 1 nearest, first is this point
			nearest = kdtree.nearest(key, k + 1);
			
			for (int j = 1; j <= k; j++) {
				kNearest[i][j - 1] = (Integer) nearest[j];
			}
			Arrays.sort(kNearest[i]);
		}
		return kNearest;
	}

	/**
	 * Construct the shared nearest neighbor graph from the sparsified
	 * similarity matrix. Graph is in matrix format.
	 * 
	 * @param matrix
	 *            K-closest matrix
	 * @return
	 */
	private void createSNNGraph(int[][] nearestNeighbors) {
		SNNGraph = new UndirectedGraph(points.size());
		int strength;
		for (int i = 0; i < points.size(); i++) {
			for (int j : nearestNeighbors[i]) {
				// no need to compare j & i if already compared i & j, j neighbors must contain i
				if (j <= i || Arrays.binarySearch(nearestNeighbors[j], i) < 0) {
					continue;
				}

				strength = 0;
				for (int k : nearestNeighbors[i]) {
					if (Arrays.binarySearch(nearestNeighbors[j], k) >= 0) {
						strength++;
					}
				}
				SNNGraph.addEdge(i, j, strength);
			}
		}
	}

	/**
	 * Find the SNN density of each point
	 * 
	 * @param matrix
	 * @return
	 */
	private int[] calcSNNDensity() {
		int[] links = new int[points.size()];
		for (int i = 0; i < links.length; i++) {
			links[i] = 0;
			for (Edge edge : SNNGraph.getVertexEdges(i)) {
				if (edge.getStrength() >= eps) {
					links[i]++;
				}
			}
		}
		return links;
	}

	/**
	 * Find the core points.
	 * 
	 * @param similarityMatrix
	 */
	private void findCorePoints() {
		int[] SNNDensity = calcSNNDensity();
		for (int i = 0; i < SNNDensity.length; i++) {
			points.get(i).setCore(SNNDensity[i] >= minPts);
			points.get(i).setCluster(-1);
		}
	}

	/**
	 * Form clusters from the core points.
	 * Discard all noise points.
	 * Assign all non-noise non-core points to clusters.
	 * 
	 * @param similarityMatrix
	 */
	private int formClusters() {
		int cluster = 0;
		int j, k;
		int size = points.size();
		Queue<Integer> queue = new LinkedList<Integer>();

		// BFS to connect core points into clusters
		for (int i = 0; i < size; i++) {
			if (points.get(i).getCluster() != -1 || !points.get(i).isCore()) {
				continue;
			}

			// next cluster
			cluster++;

			queue.add(i);
			points.get(i).setCluster(cluster);
			while (!queue.isEmpty()) {
				j = (int) queue.poll();
				
				for (Edge edge : SNNGraph.getVertexEdges(j)) {
					if (edge.getStrength() < eps) {
						continue;
					}
					k = edge.getTarget();
					if (points.get(k).getCluster() == -1 && points.get(k).isCore()) {
						points.get(k).setCluster(cluster);
						queue.add(k);
					}
				}
			}
		}
		
		// connect all border points to nearest core point
		DataPoint connectTo;
		int connectToStrength;
		for (int i = 0; i < size; i++) {
			if (points.get(i).getCluster() == -1) {
				connectTo = null;
				connectToStrength = 0;
				
				for (Edge edge : SNNGraph.getVertexEdges(i)) {
					if (edge.getStrength() < eps) {
						continue;
					}
					j = edge.getTarget();
					if (points.get(j).isCore() && (connectTo == null || connectToStrength < edge.getStrength())) {
						connectTo = points.get(j);
						connectToStrength = edge.getStrength();
					}
				}

				points.get(i).setCluster(connectTo == null ? 0 : connectTo.getCluster());
			}
		}

		return cluster;
	}
}
