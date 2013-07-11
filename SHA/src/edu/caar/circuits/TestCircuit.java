package edu.caar.circuits;

import java.util.ArrayList;
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
public class TestCircuit extends BooleanCircuit {

	private static final long serialVersionUID = 2908552618552078249L;

	/**
	 * Constructs and initializes circuit
	 */
	public TestCircuit() {
		initializeGraph();
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		// Add input vertices
		for (int i = 0; i < 4; i++) {
			addVertex(GateFactory.getInputNode());
		}

		// Add output vertices
		for (int i = 0; i < 4; i++) {
			addVertex(GateFactory.getOutputNode());
		}

		// Add and gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getAndGate());
		}

		// Add or gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getOrGate());
		}

		// Add xor gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getXorGate());
		}

		// Add edges to create linking structure
		addEdge(new Edge(), inputNodes.get(0), andGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(1), andGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(2), orGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(3), xorGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), andGates.get(0), orGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), orGates.get(0), outputNodes.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), orGates.get(0), xorGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), xorGates.get(0), outputNodes.get(1),
				EdgeType.DIRECTED);
		addEdge(new Edge(), xorGates.get(0), outputNodes.get(2),
				EdgeType.DIRECTED);
		addEdge(new Edge(), xorGates.get(0), outputNodes.get(3),
				EdgeType.DIRECTED);
	}

	/**
	 * Returns string representation of circuit
	 */
	public String toString() {
		return super.toString();
	}

	public void generateInputs(List<Gate> variableInputs, List<String> inputs) {
		if (variableInputs.size() > 0) {
			Gate input = variableInputs.get(0);
			if (variableInputs.size() == 1) {
				input.setValue(false);
				inputs.add(BooleanCircuit.booleanListToString(getInput()));
				input.setValue(true);
				inputs.add(BooleanCircuit.booleanListToString(getInput()));
			} else {
				List<Gate> removed = new ArrayList<Gate>(variableInputs);
				removed.remove(0);
				input.setValue(false);
				generateInputs(removed, inputs);
				input.setValue(true);
				generateInputs(removed, inputs);
			}
		}
	}

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create circuits
		TestCircuit circuit = new TestCircuit();
		TestCircuit tempCircuit = new TestCircuit();

		// Display circuit
		new DisplayCircuit(tempCircuit).display();

		// Hardcode inputs
		tempCircuit.minCutEvaluateCircuit();

		// Display circuit
		new DisplayCircuit(tempCircuit).display();

		// Simplify circuit
		List<Gate> variableInputs = tempCircuit.simplifyCircuit();
		System.out.println("Variable Inputs: " + variableInputs);

		// Display circuit
		new DisplayCircuit(tempCircuit).display();

		// Print collisions
		List<String> inputs = new ArrayList<String>((int) Math.pow(2,
				variableInputs.size()));
		tempCircuit.generateInputs(variableInputs, inputs);
		List<String> outputs = new ArrayList<String>((int) Math.pow(2,
				variableInputs.size()));
		for (String input : inputs) {
			outputs.add(BooleanCircuit.booleanListToString(circuit
					.getOutput(BooleanCircuit.StringToBooleanList(input))));
		}
		System.out.println("Inputs with collisions: " + inputs + " --> "
				+ outputs);
	}

}
