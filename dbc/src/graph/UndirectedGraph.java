package graph;

import java.util.ArrayList;

/**
 * Simple undirected graph implementation.
 */
public class UndirectedGraph {

	/**
	 * Edges from vertices
	 */
	private ArrayList<Edge>[] vertices;

	@SuppressWarnings("unchecked")
	public UndirectedGraph(int verticesNumber) {
		vertices = new ArrayList[verticesNumber];
		for (int i = 0; i < verticesNumber; i++) {
			vertices[i] = new ArrayList<Edge>();
		}
	}

	/**
	 * Add edge to graph
	 * 
	 * @param vertex1
	 * @param vertex2
	 * @param strength
	 */
	public void addEdge(int vertex1, int vertex2, int strength) {
		Edge edge = new Edge(vertex1, vertex2, strength);
		vertices[vertex1].add(edge);
		Edge edge2 = new Edge(vertex2, vertex1, strength);
		vertices[vertex2].add(edge2);
	}

	/**
	 * Get edges of vertex
	 * 
	 * @param vertex
	 * @return
	 */
	public ArrayList<Edge> getVertexEdges(int vertex) {
		return vertices[vertex];
	}

}
