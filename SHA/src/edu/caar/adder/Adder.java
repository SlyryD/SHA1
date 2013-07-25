package edu.caar.adder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.caar.circuit.BooleanCircuit;
import edu.caar.circuit.Edge;
import edu.caar.circuit.Gate;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models circuit for adding two bits and keeps track of overflow
 * 
 * @author Ryan
 */
public class Adder extends BooleanCircuit {

	// Serialization ID
	private static final long serialVersionUID = -87857988086683485L;

	HashMap<String, String> table;

	/**
	 * Constructs default adder
	 */
	public Adder() {
		super();
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
		List<Boolean> input1 = intToBooleanList(num1), input2 = intToBooleanList(num2);
		initializeGraph();
		setInput(input1, input2);
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		List<Gate> input1 = new ArrayList<Gate>(32), input2 = new ArrayList<Gate>(
				32);
		for (int i = 0; i < 32; i++) {
			input1.add(getInputNode());
		}
		for (int i = 0; i < 32; i++) {
			input2.add(getInputNode());
		}
		List<Gate> output = add(input1, input2);
		for (Gate gate : output) {
			addEdge(new Edge(), gate, getOutputNode(), EdgeType.DIRECTED);
		}
	}

	/**
	 * Creates circuit which adds two 32-bit numbers (mod 2^32). Does not modify
	 * original lists.
	 * 
	 * @param input1
	 * @param input2
	 */
	public List<Gate> add(List<Gate> input1, List<Gate> input2) {
		if (input1.size() != 32 || input2.size() != 32) {
			throw new IllegalArgumentException("Input invalid length");
		}
		List<Gate> outputGates = new ArrayList<Gate>(32);
		// Half adder
		Gate xor;
		outputGates.add(0, xor(input1.get(31), input2.get(31)));
		Gate carryover = and(input1.get(31), input2.get(31));
		// Full adders
		for (int i = 30; i > 0; i--) {
			xor = xor(input1.get(i), input2.get(i));
			outputGates.add(0, xor(xor, carryover));
			carryover = or(and(input1.get(i), input2.get(i)),
					and(xor, carryover));
		}
		// Final adder
		outputGates.add(0, xor(xor(input1.get(0), input2.get(0)), carryover));
		// Return output gates
		return outputGates;
	}

	public void setInput(List<Boolean> input1, List<Boolean> input2) {
		for (int i = 0; i < 32; i++) {
			setValue(inputNodes.get(i), input1.get(i));
		}
		for (int i = 0; i < 32; i++) {
			setValue(inputNodes.get(i + 32), input2.get(i));
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
		for (int i = 0; i < inputNodes.size(); i++) {
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

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create adder
		Adder temp = new Adder(64, 0);
		System.out.println(booleanListToString(temp.getGateValues(temp
				.getInputNodes())));
		System.out.println(booleanListToString(temp.getOutput()));

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
