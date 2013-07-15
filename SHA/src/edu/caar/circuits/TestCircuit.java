package edu.caar.circuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.caar.sha.jung.BooleanCircuit;
import edu.caar.sha.jung.Edge;
import edu.caar.sha.jung.Gate;
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
		// Add edges to create linking structure
		Gate or = or(getInputNode(), and(getInputNode(), getInputNode()));
		Gate xor = xor(getInputNode(), or);

		addEdge(new Edge(), or, getOutputNode(), EdgeType.DIRECTED);
		addEdge(new Edge(), xor, getOutputNode(), EdgeType.DIRECTED);
		addEdge(new Edge(), xor, getOutputNode(), EdgeType.DIRECTED);
		addEdge(new Edge(), xor, getOutputNode(), EdgeType.DIRECTED);
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
				inputs.add(booleanListToString(getInput()));
				input.setValue(true);
				inputs.add(booleanListToString(getInput()));
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

	public static boolean birthdayAttack(BooleanCircuit circuit) {
		// Number of terms to search 2^n/2 where n = 4
		int numTerms = (int) Math.pow(2, 2); // 4 terms
		int count = 0;
		HashMap<String, String> table;
		String[] inputs;
		String minCutValues;

		while (count < 10) {
			table = new HashMap<String, String>();
			count += 1;
			System.out.println("Iteration number " + count);
			// Generate 2^(n/2) random terms out of 2^4 terms
			System.out
					.println("Generating " + numTerms + " random messages...");
			inputs = new String[numTerms];
			for (int i = 0; i < numTerms; i++) {
				inputs[i] = generateInput(circuit);
			}
			System.out.println("All random messages generated.");
			// Hash all the terms in the term_array
			System.out.println("Hashing all random messages...");
			for (String input : inputs) {
				minCutValues = booleanListToString(circuit
						.getMinCutValues(input));
				String value = table.get(minCutValues);
				if (value != null && !value.equals(input)) {
					System.out.println("Collision detected!\n" + value
							+ " --> "
							+ booleanListToString(circuit.getOutput(value))
							+ "\n" + input + " --> "
							+ booleanListToString(circuit.getOutput(input)));
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
		TestCircuit tempCircuit;
		TestCircuit circuit = new TestCircuit();

		// for (int i = 0; i < 10; i++) {
		// circuit.minCutSetInput();
		// System.out.println("---Hardcoded Gates---");
		// for (Gate gate : circuit.getInputNodes()) {
		// if (gate.isEvaluated()) {
		// System.out.println(gate);
		// }
		// }
		// System.out.println(generateInput(circuit));
		// }

		// Display circuit
		// new DisplayCircuit(circuit).display();

		// Birthday attack
		System.out
				.println("---------BIRTHDAY ATTACK ON ORIGINAL CIRCUIT----------");
		while (!birthdayAttack(circuit)) {
		}

		System.out
				.println("---------BIRTHDAY ATTACK ON SIMPLIFIED CIRCUITS----------");
		do {
			// New circuit to be simplified
			tempCircuit = new TestCircuit();
			// Hardcode inputs
			tempCircuit.minCutSetInput();
			// Simplify circuit
			tempCircuit.simplifyCircuit();
		} while (!birthdayAttack(tempCircuit));

		// // Simplify circuit
		// List<Gate> variableInputs = tempCircuit.simplifyCircuit();
		// System.out.println("Variable Inputs: " + variableInputs);
		//
		// // Print collisions
		// List<String> inputs = new ArrayList<String>((int) Math.pow(2,
		// variableInputs.size()));
		// tempCircuit.generateInputs(variableInputs, inputs);
		// List<String> outputs = new ArrayList<String>((int) Math.pow(2,
		// variableInputs.size()));
		// for (String input : inputs) {
		// outputs.add(booleanListToString(circuit.getOutput(input)));
		// }
		// System.out.println("Inputs with collisions: " + inputs + " --> "
		// + outputs);
	}

}
