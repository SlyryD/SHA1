package edu.caar.sha.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models circuit for SHA-1 hashing algorithm
 * 
 * @author Ryan
 */
public class SHA1 extends BooleanCircuit {

	// Serialization ID
	private static final long serialVersionUID = -3025896134713478273L;

	/**
	 * Constructs SHA-1 circuit graph
	 */
	public SHA1() {
		super();
		initializeGraph();
	}

	/**
	 * Initializes graph of SHA-1 circuit
	 */
	public void initializeGraph() {
		// Temporary storage (X[j] in handbook)
		List<Gate> xEntry;
		List<List<Gate>> xStorage = new ArrayList<List<Gate>>(80);
		for (int i = 0; i < 16; i++) {
			xEntry = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				xEntry.add(getInputNode());
			}
			xStorage.add(xEntry);
		}

		// IV Constants (h1, .., h5)
		List<Gate> hEntry;
		List<List<Gate>> hConstants = new ArrayList<List<Gate>>(5);
		for (int i = 16; i < 21; i++) {
			hEntry = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				hEntry.add(getInputNode());
			}
			hConstants.add(hEntry);
		}
		// Working variables
		List<List<Gate>> wVariables = new ArrayList<List<Gate>>(hConstants);

		// Additive Constants (y1, .., y4)
		List<Gate> yEntry;
		List<List<Gate>> yConstants = new ArrayList<List<Gate>>(4);
		for (int i = 21; i < 25; i++) {
			yEntry = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				yEntry.add(getInputNode());
			}
			yConstants.add(yEntry);
		}

		// Expand 16 words to 80 words
		for (int i = 16; i < 80; i++) {
			// Calculate (X[i - 3] XOR X[i - 8] XOR X[i - 14] XOR X[i - 16]),
			// rotate left 1 bit, and store in X[i]
			xStorage.add(rotl(
					xor(xStorage.get(i - 3),
							xor(xStorage.get(i - 8),
									xor(xStorage.get(i - 14),
											xStorage.get(i - 16)))), 1));
		}

		// Temporary list of 32 gates
		List<Gate> tList;

		// Round 1
		for (int i = 0; i < 20; i++) {
			// t := (rotl5(A) + f(B, C, D) + E + X[i] + y1)
			tList = add(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					add(fFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(new ArrayList<Gate>(wVariables.get(4)),
									add(xStorage.get(i), yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
			wVariables.set(0, tList);
		}

		// Round 2
		for (int i = 20; i < 40; i++) {
			// t := (rotl5(A) + h(B, C, D) + E + X[i] + y2)
			tList = add(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					add(hFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
			wVariables.set(0, tList);
		}

		// Round 3
		for (int i = 40; i < 60; i++) {
			// t := (rotl5(A) + g(B, C, D) + E + X[i] + y3)
			tList = add(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					add(gFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
			wVariables.set(0, tList);
		}

		// Round 4
		for (int i = 60; i < 80; i++) {
			// t := (rotl5(A) + h(B, C, D) + E + X[i] + y4)
			tList = add(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					add(hFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
			wVariables.set(0, tList);
		}

		// Update chaining variables
		List<Gate> hValue;
		for (int i = 0; i < 5; i++) {
			hValue = add(hConstants.get(i), wVariables.get(i));
			for (int j = 0; j < 32; j++) {
				addEdge(new Edge(), hValue.get(j), getOutputNode(),
						EdgeType.DIRECTED);
			}
		}
	}

	/**
	 * Creates circuit which adds two 32-bit numbers (mod 2^32)
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

	/**
	 * f(B, C, D) = ((B and C) or (not(B) and D))
	 * 
	 * @param input1
	 * @param input2
	 * @param input3
	 * @return f(input1, input2, input3)
	 */
	public List<Gate> fFunction(List<Gate> input1, List<Gate> input2,
			List<Gate> input3) {
		return or(and(input1, input2), and(not(input1), input3));
	}

	/**
	 * g(B, C, D) = ((B and C) or (B and D) or (C and D))
	 * 
	 * @param input1
	 * @param input2
	 * @param input3
	 * @return g(input1, input2, input3)
	 */
	public List<Gate> gFunction(List<Gate> input1, List<Gate> input2,
			List<Gate> input3) {
		return or(and(input1, input2),
				or(and(input1, input3), and(input2, input3)));
	}

	/**
	 * h(B, C, D) = (B xor C xor D)
	 * 
	 * @param input1
	 * @param input2
	 * @param input3
	 * @return h(input1, input2, input3)
	 */
	public List<Gate> hFunction(List<Gate> input1, List<Gate> input2,
			List<Gate> input3) {
		return xor(input1, xor(input2, input3));
	}

	/**
	 * Returns list of gates rotated left given numbers
	 * 
	 * @param list
	 * @param number
	 * @return rotl(list, number)
	 */
	public List<Gate> rotl(List<Gate> list, int number) {
		List<Gate> newList = new ArrayList<Gate>(list.subList(number, 32));
		newList.addAll(list.subList(0, number));
		return newList;
	}

	public void birthdayAttack() {
		// Number of terms to search 2^n/2 where n = 4
		int numTerms = (int) Math.pow(2, 2); // 4 terms
		int count = 0;
		HashMap<String, String> table;
		String[] inputs;
		String minCutValues;

		while (true) {
			table = new HashMap<String, String>();
			count += 1;
			System.out.println("Iteration number " + count);
			// Generate 2^(n/2) random terms out of 2^4 terms
			System.out
					.println("Generating " + numTerms + " random messages...");
			inputs = new String[numTerms];
			for (int i = 0; i < numTerms; i++) {
				inputs[i] = booleanListToString(generateInput());
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
					return;
				} else {
					table.put(minCutValues, input);
				}
			}
			System.out.println("All messages hashed.");
			System.out
					.println("No collisions found. Trying with 2^2 new random terms.");
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

	/**
	 * Generates random input of random size
	 * 
	 * @return input
	 */
	public static List<Boolean> generateInput() {
		return generateInput(getRandInt(448));
	}

	/**
	 * Generates random input of given size
	 * 
	 * @return input
	 */
	public static List<Boolean> generateInput(int size) {
		// Construct valid random input
		List<Boolean> input = new ArrayList<Boolean>();
		// Message
		for (int i = 0; i < size; i++) {
			input.add(getRandBoolean());
		}
		// Padding
		input.add(true);
		for (int i = size + 1; i < 448; i++) {
			input.add(false);
		}
		// 64-bit representation of input length
		String binaryString = Integer.toBinaryString(size);
		for (int i = 0; i < 32; i++) {
			input.add(false);
		}
		for (int i = 0; i < 32 - binaryString.length(); i++) {
			input.add(false);
		}
		for (int i = 0; i < binaryString.length(); i++) {
			input.add(binaryString.charAt(i) == '1' ? true : false);
		}

		addConstants(input);

		return input;
	}

	/**
	 * Generates input from given string
	 * 
	 * @return input
	 */
	public static List<Boolean> generateInput(String string) {
		if (string.length() > 55) {
			throw new IllegalArgumentException(
					"String longer than 447 bits not supported");
		}
		// Construct valid input from string
		List<Boolean> input = new ArrayList<Boolean>();
		// Number of bits in message
		int size = string.length() * 8;
		// Message
		String binaryStr;
		for (int i = 0; i < string.length(); i++) {
			binaryStr = Integer.toBinaryString(string.charAt(i));
			for (int j = 0; j < 8 - binaryStr.length(); j++) {
				input.add(false);
			}
			for (int j = 0; j < binaryStr.length(); j++) {
				input.add(binaryStr.charAt(j) == '1' ? true : false);
			}
		}
		// Padding
		input.add(true);
		for (int i = size + 1; i < 448; i++) {
			input.add(false);
		}
		// 64-bit representation of input length
		binaryStr = Integer.toBinaryString(size);
		for (int i = 0; i < 32; i++) {
			input.add(false);
		}
		for (int i = 0; i < 32 - binaryStr.length(); i++) {
			input.add(false);
		}
		for (int i = 0; i < binaryStr.length(); i++) {
			input.add(binaryStr.charAt(i) == '1' ? true : false);
		}

		addConstants(input);

		return input;
	}

	public static void addConstants(List<Boolean> input) {
		// IV constants
		String[] hConstants = new String[] {
				Integer.toBinaryString(0x67452301),
				Integer.toBinaryString(0xefcdab89),
				Integer.toBinaryString(0x98badcfe),
				Integer.toBinaryString(0x10325476),
				Integer.toBinaryString(0xc3d2e1f0) };
		for (String h : hConstants) {
			for (int i = 0; i < 32 - h.length(); i++) {
				input.add(false);
			}
			for (int i = 0; i < h.length(); i++) {
				input.add(h.charAt(i) == '1' ? true : false);
			}
		}
		// Additive constants
		String[] yConstants = new String[] {
				Integer.toBinaryString(0x5a827999),
				Integer.toBinaryString(0x6ed9eba1),
				Integer.toBinaryString(0x8f1bbcdc),
				Integer.toBinaryString(0xca62c1d6) };
		for (String y : yConstants) {
			for (int i = 0; i < 32 - y.length(); i++) {
				input.add(false);
			}
			for (int i = 0; i < y.length(); i++) {
				input.add(y.charAt(i) == '1' ? true : false);
			}
		}
	}

	/**
	 * Creates SHA-1 circuit, gathers output
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Get circuit
		SHA1 circuit = new SHA1();

		// Construct empty string input
		List<Boolean> input = generateInput("abc");

		// Valid randomly generated input
		// List<Boolean> generatedInput = SHA1.generateInput();
		// System.out.println(booleanListToString(generatedInput));

		// Min-cut
		// System.out.println("Min-cut");
		// System.out.println("The edge set is: " + circuit.getMinCutEdges());

		// Input
		System.out.println("Input:");
		System.out.println(binarytoHexString(booleanListToString(input)));
		// Get output, should be da39a3ee5e6b4b0d3255bfef95601890afd80709
		System.out.println("Output:");
		System.out.println(binarytoHexString(booleanListToString(circuit
				.getOutput(input))));
	}

}
