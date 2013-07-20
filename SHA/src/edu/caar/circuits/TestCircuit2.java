package edu.caar.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.caar.sha.jung.BooleanCircuit;
import edu.caar.sha.jung.DisplayCircuit;
import edu.caar.sha.jung.Edge;
import edu.caar.sha.jung.Gate;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models circuit for adding two bits and keeps track of overflow
 * 
 * @author Ryan
 */
public class TestCircuit2 extends BooleanCircuit {

	private static final long serialVersionUID = 2908552618552078249L;

	HashMap<String, String> table;

	/**
	 * Constructs and initializes circuit
	 */
	public TestCircuit2() {
		initializeGraph();
		table = new HashMap<String, String>();
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		// Add edges to create linking structure
		Gate input = getInputNode();
		Gate gate = xor(xor(getInputNode(), input), input);

		addEdge(new Edge(), gate, getOutputNode(), EdgeType.DIRECTED);
	}

	/**
	 * Returns string representation of circuit
	 */
	public String toString() {
		return super.toString();
	}

	/**
	 * Sets first few bites of inputs
	 */
	public void fixInput() {
		resetAllGates();
		int count = 0;
		for (int i = 0; i < inputNodes.size(); i++) {
			if (getRandBoolean()) {
				count++;
				setAndFixValue(inputNodes.get(i), getRandBoolean());
			}
		}
		System.out.println(count + " gates fixed.");
	}

	public void generateInputs(List<Gate> variableInputs, List<String> inputs) {
		if (variableInputs.size() > 0) {
			Gate input = variableInputs.get(0);
			if (variableInputs.size() == 1) {
				values.put(input, false);
				inputs.add(booleanListToString(getGateValues(inputNodes)));
				values.put(input, true);
				inputs.add(booleanListToString(getGateValues(inputNodes)));
			} else {
				List<Gate> removed = new ArrayList<Gate>(variableInputs);
				removed.remove(0);
				values.put(input, false);
				generateInputs(removed, inputs);
				values.put(input, true);
				generateInputs(removed, inputs);
			}
		}
	}

	public boolean birthdayAttack() {
		// Number of terms to search 2^n/2 where n = 4
		int numTerms = (int) Math.pow(2, 2); // 4 terms
		int count = 0;
		String[] inputs;
		String minCutValues;

		while (count < 10) {
			count += 1;
			System.out.println("Iteration number " + count);
			// Generate 2^(n/2) random terms out of 2^4 terms
			System.out
					.println("Generating " + numTerms + " random messages...");
			inputs = new String[numTerms];
			for (int i = 0; i < numTerms; i++) {
				inputs[i] = generateInput();
			}
			System.out.println("All random messages generated.");
			// Hash all the terms in the term_array
			System.out.println("Hashing all random messages...");
			for (String input : inputs) {
				minCutValues = booleanListToString(getMinCutValues(input));
				String value = table.get(minCutValues);
				if (value != null && !value.equals(input)) {
					System.out.println("Collision detected!\n" + value
							+ " --> " + booleanListToString(getOutput(value))
							+ "\n" + input + " --> "
							+ booleanListToString(getOutput(input)));
					return true;
				} else {
					table.put(minCutValues, input);
				}
			}
			System.out.println("All messages hashed.");
			System.out
					.println("No collisions found. Trying with 2^2 new random terms.");
		}
		return false;
	}

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create circuits
		TestCircuit2 circuit = new TestCircuit2();

		// Fix circuit input
//		circuit.fixInput();
		circuit.setAndFixValue(circuit.getInputNodes().get(1), false);

		// Simplify circuit
		List<Gate> variableInputs = circuit.simplifyCircuit();
		System.out.println("Variable Inputs: " + variableInputs);

		// Display circuit
		new DisplayCircuit(circuit).display();

		// // Print collisions
		// List<String> inputs = new ArrayList<String>((int) Math.pow(2,
		// variableInputs.size()));
		// circuit.generateInputs(variableInputs, inputs);
		// List<String> outputs = new ArrayList<String>((int) Math.pow(2,
		// variableInputs.size()));
		// for (String input : inputs) {
		// outputs.add(booleanListToString(circuit.getOutput(input)));
		// }
		// System.out.println("Inputs with collisions: " + inputs + " --> "
		// + outputs);
	}

}
