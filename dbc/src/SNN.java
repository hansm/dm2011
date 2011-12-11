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
	private int K;

	/**
	 * Links strength threshold for core point
	 */
	private int coreThreshold;

	/**
	 * Links strength threshold for noise point, noise points lower than this
	 */
	private int noiseThreshold;

	/**
	 * Link strength threshold
	 */
	private int linkThreshold;

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
		
		fillKDTree();

		createSNNGraph(getKNearest());

		findCoreAndNoise();

		removeUnimportantLinks();

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
		int[][] kNearest = new int[points.size()][K];
		double[] key = new double[2];
		Object[] nearest;
		for (int i  = 0; i < points.size(); i++) {
			key[0] = points.get(i).getX();
			key[1] = points.get(i).getY();

			// get K + 1 nearest, first is this point
			nearest = kdtree.nearest(key, K + 1);
			
			for (int j = 1; j <= K; j++) {
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
	 * For every data point in the graph, calculate the total strength of links
	 * coming out of the point.
	 * 
	 * @param matrix
	 * @return
	 */
	private int[] calcLinksTotal() {
		int[] links = new int[points.size()];
		for (int i = 0; i < links.length; i++) {
			links[i] = 0;
			for (Edge edge : SNNGraph.getVertexEdges(i)) {
				links[i] += edge.getStrength();
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
	private void findCoreAndNoise() {
		int[] linksTotal = calcLinksTotal();
		for (int i = 0; i < linksTotal.length; i++) {
			// check if is core
			points.get(i).setCore(linksTotal[i] >= coreThreshold);

			// check if is noise
			points.get(i).setCluster(linksTotal[i] < noiseThreshold ? 0 : -1);
		}
	}

	/**
	 * Remove all links between points that have weight smaller than a threshold
	 */
	private void removeUnimportantLinks() {
		ArrayList<Edge> edges;
		for (int i = 0; i < points.size(); i++) {
			edges = SNNGraph.getVertexEdges(i);
			for (int j = 0; j < edges.size(); j++) {
				if (edges.get(j).getStrength() < linkThreshold)  {
					edges.set(j, null);
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
					if (edge == null) {
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
					if (edge == null) {
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
