package edu.caar.circuit;

public class Edge {

	public static final int CAPACITY = 1;
	public static int edgeCount = 0;
	private int number;

	public Edge() {
		number = edgeCount++;
	}

	public String toString() {
		return "E" + number;
	}

}
