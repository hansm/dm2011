/**
 * Clustering algorithm interface
 */
public interface ClusteringAlgorithm {

	/**
	 * Run clustering algorithm
	 * 
	 * @return number of clusters
	 * @throws AlgorithmException TODO
	 */
	public abstract int run() throws AlgorithmException;

}