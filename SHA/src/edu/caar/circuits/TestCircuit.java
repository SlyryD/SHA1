package edu.caar.circuits;

import java.util.Iterator;
import java.util.List;

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
public class TestCircuit {

	// Boolean circuit to model adder
	BooleanCircuit circuit;

	/**
	 * Constructs and initializes circuit
	 */
	public TestCircuit() {
		circuit = new BooleanCircuit();
		initializeGraph();
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		// Add input vertices
		for (int i = 0; i < 4; i++) {
			circuit.addVertex(GateFactory.getInputNode());
		}
		List<Gate> inputNodes = circuit.getInputNodes();

		// Add output vertices
		for (int i = 0; i < 4; i++) {
			circuit.addVertex(GateFactory.getOutputNode());
		}
		List<Gate> outputNodes = circuit.getOutputNodes();

		// Add and gates
		for (int i = 0; i < 1; i++) {
			circuit.addVertex(GateFactory.getAndGate());
		}
		List<Gate> andGates = circuit.getAndGates();

		// Add or gates
		for (int i = 0; i < 1; i++) {
			circuit.addVertex(GateFactory.getOrGate());
		}
		List<Gate> orGates = circuit.getOrGates();

		// Add xor gates
		for (int i = 0; i < 1; i++) {
			circuit.addVertex(GateFactory.getXorGate());
		}
		List<Gate> xorGates = circuit.getXorGates();

		// Add edges to create linking structure
		circuit.addEdge(new Edge(), inputNodes.get(0), andGates.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), inputNodes.get(1), andGates.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), inputNodes.get(2), orGates.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), inputNodes.get(3), xorGates.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), andGates.get(0), orGates.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), orGates.get(0), outputNodes.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), orGates.get(0), xorGates.get(0),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), xorGates.get(0), outputNodes.get(1),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), xorGates.get(0), outputNodes.get(2),
				EdgeType.DIRECTED);
		circuit.addEdge(new Edge(), xorGates.get(0), outputNodes.get(3),
				EdgeType.DIRECTED);
	}

	/**
	 * Returns adder circuit
	 * 
	 * @return circuit
	 */
	public BooleanCircuit getCircuit() {
		return circuit;
	}

	/**
	 * Sets input into full adder circuit
	 * 
	 * @param bit1
	 * @param bit2
	 * @param bit3
	 * @param bit4
	 */
	public void setInput(boolean bit1, boolean bit2, boolean bit3, boolean bit4) {
		Iterator<Gate> gates = circuit.getInputNodes().iterator();
		gates.next().setValue(bit1);
		gates.next().setValue(bit2);
		gates.next().setValue(bit3);
		gates.next().setValue(bit4);
	}
	
	

	/**
	 * Returns string representation of circuit
	 */
	public String toString() {
		return circuit.toString();
	}

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create adder
		TestCircuit testCircuit = new TestCircuit();

		// Get circuit
		BooleanCircuit circuit = testCircuit.getCircuit();
		circuit.evaluateCircuit();

		// Print edges
		System.out.println(testCircuit.toString());

		// Min-cut
		System.out.println("Min-cut");
		System.out.println("The edge set is: " + circuit.getMinCutEdges());

		// Associate DisplayCircuit
		DisplayCircuit circuitDisplay = new DisplayCircuit(circuit);
		circuitDisplay.display();
	}

}
