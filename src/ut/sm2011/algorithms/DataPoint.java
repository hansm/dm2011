package ut.sm2011.algorithms;

public class DataPoint {

	/**
	 * Point x coordinate
	 */
	protected int x;

	/**
	 * Point y coordinate
	 */
	protected int y;
	
	/**
	 * Cluster that point belongs to, 0 for noise
	 */
	protected int cluster;
	
	/**
	 * Whether this point is core point
	 */
	protected boolean core;

	public boolean isCore() {
		return core;
	}

	public void setCore(boolean core) {
		this.core = core;
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public DataPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculate distance from this point to some other point
	 * 
	 * @param point
	 *            other point
	 * @return distance between points
	 */
	public double calcDistance(DataPoint point) {
		return Math.sqrt(Math.pow(x - point.getX(), 2) + Math.pow(y - point.getY(), 2));
	}

	public String toString() {
		return x +"\t"+ y +"\t"+ cluster +"\t"+ core;
	}
}
