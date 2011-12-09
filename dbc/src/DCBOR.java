import java.util.ArrayList;

import net.sf.javaml.core.kdtree.KDTree;


public class DCBOR implements ClusteringAlgorithm {
	private double ratio;
	private ArrayList<DataPoint> points;
	private KDTree kdtree;
	
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
			
			kdtree.insert(coords, points.get(i));
		}
	}

	@Override
	public int run() throws AlgorithmException {
		double[] arg0 = {0, 1};
		Object[] nearest = kdtree.nearest(arg0, 15);
		
		DataPoint point;
		DataPoint next = null;
		for(int i = 0; i< nearest.length; i++){
			point = (DataPoint) nearest[i];
			if(i<nearest.length-1)
				next = (DataPoint) nearest[i+1];
			System.out.println("x: "+point.getX()+" y: "+point.getY()+ "distance: " + point.calcDistance(next));
		}
		return 0;
	}

}
