package edu.caar.adders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.caar.sha.jung.BooleanCircuit;
import edu.caar.sha.jung.Edge;
import edu.caar.sha.jung.Gate;
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
		// Add edges to create linking structure
		Gate input1, input2, carryover, xor;
		input1 = getInputNode();
		input2 = getInputNode();
		addEdge(new Edge(), xor(input1, input2), getOutputNode(),
				EdgeType.DIRECTED);
		carryover = and(input1, input2);
		// Full adders
		for (int i = 1; i < size; i++) {
			input1 = getInputNode();
			input2 = getInputNode();
			xor = xor(input1, input2);
			addEdge(new Edge(), xor(xor, carryover), getOutputNode(),
					EdgeType.DIRECTED);
			carryover = or(and(input1, input2), and(xor, carryover));
		}
	}

	public void setInput(List<Boolean> input1, List<Boolean> input2) {
		ListIterator<Boolean> it1 = input1.listIterator(input1.size()), it2 = input2
				.listIterator(input2.size());
		for (int i = 0; i < size; i++) {
			values.put(inputNodes.get(2 * i),
					it1.hasPrevious() ? it1.previous() : false);
			values.put(inputNodes.get(2 * i),
					it2.hasPrevious() ? it2.previous() : false);
		}
	}

	public String getOutputString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Gate> outputIt = outputNodes.iterator();
		while (outputIt.hasNext()) {
			Gate gate = outputIt.next();
			if (outputIt.hasNext()) {
				sb.insert(0, values.get(gate) == true ? "1" : "0");
			} else if (values.get(gate) == true) {
				sb.insert(0, "1");
			}
		}
		return sb.toString();
	}

	/**
	 * Sets first few bites of inputs
	 */
	public void setInput() {
		resetValues();
		for (int i = 0; i < 9; i++) {
			values.put(inputNodes.get(i), false);
			fixed.put(inputNodes.get(i), true);
			values.put(inputNodes.get(i + 32), false);
			fixed.put(inputNodes.get(i + 32), true);
		}
	}

	public static boolean birthdayAttack(BooleanCircuit circuit) {
		// Number of terms to search 2^n/2 where n = 64
		int numTerms = (int) Math.pow(2, 16);
		int count = 0;
		HashMap<String, String> table;
		String[] inputs;
		String minCutValues;

		while (true) {
			table = new HashMap<String, String>();
			count += 1;
			System.out.println("Iteration number " + count);
			// Generate 2^(n/2) random terms out of 2^64 terms
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
			System.out.println("No collisions found. Trying with " + numTerms
					+ " new random terms.");
		}
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
		// int num1 = 2000000000, num2 = 1;

		// Create adder
		// Adder tempCircuit = new Adder();
		Adder circuit = new Adder();

		long startTime = System.nanoTime();
		circuit.setInput();
		// Birthday attack
		System.out
				.println("---------BIRTHDAY ATTACK ON ORIGINAL CIRCUIT----------");
		while (!birthdayAttack(circuit)) {
		}
		long endTime = System.nanoTime();
		System.out.println(endTime - startTime + "ns");
		//
		// // Evaluated circuit
		// tempCircuit.evaluateCircuit();
		//
		// // Print edges
		// System.out.println(tempCircuit.toString());
		//
		// // Print results
		// System.out.println(Integer.toBinaryString(num1) + " + "
		// + Integer.toBinaryString(num2) + " = "
		// + tempCircuit.getOutputString());
		// System.out.println(num1 + " + " + num2 + " = "
		// + Integer.parseInt(tempCircuit.getOutputString(), 2));
		//
		// // Min-cut
		// System.out.println("Min-cut");
		// System.out.println("The edge set is: " +
		// tempCircuit.getMinCutEdges());
		//
		// // Associate DisplayCircuit
		// // DisplayCircuit circuitDisplay = new DisplayCircuit(circuit);
		// // circuitDisplay.display();
		//
		// // Fix inputs along min cut
		// tempCircuit.setInput();
		// tempCircuit.simplifyCircuit();
		//
		// // Min-cut
		// System.out.println("Min-cut");
		// System.out.println("The edge set is: " +
		// tempCircuit.getMinCutEdges());
	}

}
