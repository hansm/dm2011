package graph;

/**
 * Edge with strength for graph
 */
public class Edge {

	/**
	 * Target of edge
	 */
	private int target;

	/**
	 * Edge strength
	 */
	private int strength;

	public int getTarget() {
		return target;
	}

	public int getStrength() {
		return strength;
	}

	public Edge(int target, int strength) {
		this.target = target;
		this.strength = strength;
	}

}
