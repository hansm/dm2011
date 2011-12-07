import java.util.ArrayList;

public class DBSCAN implements ClusteringAlgorithm {
	private int minPts;
	private double eps;
	private ArrayList <DataPoint> points;
	
	public DBSCAN(ArrayList <DataPoint> points, int minPts, double eps){
		this.minPts = minPts;
		this.eps = eps;
		this.points = points;
		computeSimilarityMatrix();
		
		//initialize all points as unvisited (cluster = -1)
		for(int i = 0; i < points.size(); i++)
			points.get(i).cluster = -1;
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

	@Override
	public int run() throws AlgorithmException {
		// TODO Auto-generated method stub
		return 0;
	}

}