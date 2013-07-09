package edu.caar.adders;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.caar.sha.jung.BooleanCircuit;
import edu.caar.sha.jung.DisplayCircuit;
import edu.caar.sha.jung.Edge;
import edu.caar.sha.jung.Gate;
import edu.caar.sha.jung.GateFactory;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models circuit for adding two bits and keeps track of overflow
 * 
 * @author Ryan
 */
public class Adder extends BooleanCircuit {

	// Serialization ID
	private static final long serialVersionUID = -87857988086683485L;

	private int size;

	/**
	 * Constructs default adder
	 */
	public Adder() {
		super();
		size = 32;
		initializeGraph();
	}

	/**
	 * Constructs adder with given inputs
	 * 
	 * @param size
	 */
	public Adder(int num1, int num2) {
		super();
		List<Boolean> input1 = BooleanCircuit.intToBooleanList(num1), input2 = BooleanCircuit
				.intToBooleanList(num2);
		size = Math.max(input1.size(), input2.size());
		initializeGraph();
		setInput(input1, input2);
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		// Add input vertices
		for (int i = 0; i < 2 * size; i++) {
			addVertex(GateFactory.getInputNode());
		}

		// Add output vertices
		for (int i = 0; i < size + 1; i++) {
			addVertex(GateFactory.getOutputNode());
		}

		// Add and gates
		for (int i = 0; i < 2 * (size - 1) + 1; i++) {
			addVertex(GateFactory.getAndGate());
		}
		Iterator<Gate> andIt = andGates.iterator();

		// Add or gates
		for (int i = 0; i < size - 1; i++) {
			addVertex(GateFactory.getOrGate());
		}
		Iterator<Gate> orIt = orGates.iterator();

		// Add xor gates
		for (int i = 0; i < 2 * (size - 1) + 1; i++) {
			addVertex(GateFactory.getXorGate());
		}
		Iterator<Gate> xorIt = xorGates.iterator();

		// Add edges to create linking structure
		Gate output = xorIt.next(), carryover = andIt.next(), xor, and1, and2;
		addEdge(new Edge(), output, outputNodes.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(0), output, EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(1), output, EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(0), carryover, EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(1), carryover, EdgeType.DIRECTED);
		// Full adders
		for (int i = 1; i < size; i++) {
			xor = xorIt.next();
			output = xorIt.next();
			addEdge(new Edge(), output, outputNodes.get(i), EdgeType.DIRECTED);
			and1 = andIt.next();
			and2 = andIt.next();
			addEdge(new Edge(), inputNodes.get(2 * i), xor, EdgeType.DIRECTED);
			addEdge(new Edge(), inputNodes.get(2 * i + 1), xor,
					EdgeType.DIRECTED);
			addEdge(new Edge(), inputNodes.get(2 * i), and1, EdgeType.DIRECTED);
			addEdge(new Edge(), inputNodes.get(2 * i + 1), and1,
					EdgeType.DIRECTED);
			addEdge(new Edge(), xor, output, EdgeType.DIRECTED);
			addEdge(new Edge(), carryover, output, EdgeType.DIRECTED);
			addEdge(new Edge(), xor, and2, EdgeType.DIRECTED);
			addEdge(new Edge(), carryover, and2, EdgeType.DIRECTED);
			carryover = orIt.next();
			addEdge(new Edge(), and2, carryover, EdgeType.DIRECTED);
			addEdge(new Edge(), and1, carryover, EdgeType.DIRECTED);
		}
		// Output
		addEdge(new Edge(), carryover, outputNodes.get(size), EdgeType.DIRECTED);
	}

	public void setInput(List<Boolean> input1, List<Boolean> input2) {
		ListIterator<Boolean> it1 = input1.listIterator(input1.size()), it2 = input2
				.listIterator(input2.size());
		for (int i = 0; i < size; i++) {
			inputNodes.get(2 * i).setValue(
					it1.hasPrevious() ? it1.previous() : false);
			inputNodes.get(2 * i + 1).setValue(
					it2.hasPrevious() ? it2.previous() : false);
		}
	}

	public String getOutputString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Gate> outputIt = outputNodes.iterator();
		while (outputIt.hasNext()) {
			Gate gate = outputIt.next();
			if (outputIt.hasNext()) {
				sb.insert(0, gate.getValue() == true ? "1" : "0");
			} else if (gate.getValue() == true) {
				sb.insert(0, "1");
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// String representations of numbers to be added
		int num1 = 2000000000, num2 = 1;

		// Create adder
		Adder circuit = new Adder(num1, num2);

		// Evaluated circuit
		circuit.evaluateCircuit();

		// Print edges
		System.out.println(circuit.toString());

		// Print results
		System.out.println(Integer.toBinaryString(num1) + " + "
				+ Integer.toBinaryString(num2) + " = "
				+ circuit.getOutputString());
		System.out.println(num1 + " + " + num2 + " = "
				+ Integer.parseInt(circuit.getOutputString(), 2));

		// Min-cut
		System.out.println("Min-cut");
		System.out.println("The edge set is: " + circuit.getMinCutEdges());

		// Associate DisplayCircuit
		DisplayCircuit circuitDisplay = new DisplayCircuit(circuit);
		circuitDisplay.display();
	}

}
