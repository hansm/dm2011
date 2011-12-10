package graph;

public class Edge {
	
	private int source;
	
	private int target;
	
	private int strength;
	
	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}

	public int getStrength() {
		return strength;
	}

	public Edge(int source, int target, int strength) {
		this.source = source;
		this.target = target;
		this.strength = strength;
	}
	
}
