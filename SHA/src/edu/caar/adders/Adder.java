package edu.caar.adders;

import java.util.HashMap;
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

	HashMap<String, String> table;

	/**
	 * Constructs default adder
	 */
	public Adder() {
		super();
		size = 32;
		initializeGraph();
		table = new HashMap<String, String>();
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
			setAndFixValue(inputNodes.get(2 * i),
					it1.hasPrevious() ? it1.previous() : false);
			setAndFixValue(inputNodes.get(2 * i),
					it2.hasPrevious() ? it2.previous() : false);
		}
	}

	public String getOutputString() {
		StringBuilder sb = new StringBuilder();
		for (Gate gate : outputNodes) {
			if (getValue(gate) != null) {
				sb.insert(0, getValue(gate));
			}
		}
		return sb.toString();
	}

	/**
	 * Sets first few bites of inputs
	 */
	public void fixInput() {
		resetAllGates();
		int count = 0;
		for (int i = 0; i < 64; i++) {
			if (getRandBoolean()) {
				count++;
				setAndFixValue(inputNodes.get(i), getRandBoolean());
			}
		}
		System.out.println(count + " gates fixed.");
	}

	public boolean birthdayAttack() {
		// Number of terms to search 2^n/2 where n = 64
		int numTerms = (int) Math.pow(2, 16);
		int count = 0;

		String[] inputs;
		String minCutValues;

		while (true) {
			count += 1;
			System.out.println("Iteration number " + count);
			// Generate 2^(n/2) random terms out of 2^64 terms
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
		// Create adder
		System.out.println("Constructing circuit...");
		Adder circuit = new Adder();

		// Fix input and simplify circuit
		System.out.println("Fixing input and simplifying circuit...");
		circuit.fixInput();
		circuit.simplifyCircuit();

		// Calculate min cut
		System.out.println("Calculating min cut...");
		System.out.println(circuit.getMinCutEdges());

		// Birthday attack
		System.out.println("Carrying out birthday attack...");
		long startTime = System.nanoTime();
		circuit.birthdayAttack();
		long endTime = System.nanoTime();
		System.out.println(endTime - startTime + "ns");
	}

}
