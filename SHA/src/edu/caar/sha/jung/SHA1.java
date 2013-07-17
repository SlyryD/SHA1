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

	// Hash table for birthday attack
	HashMap<String, String> table;

	/**
	 * Constructs SHA-1 circuit graph
	 */
	public SHA1() {
		super();
		initializeGraph();
		table = new HashMap<String, String>();
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
					rotl(wVariables.get(0), 5),
					add(fFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(0)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j >= 0; j--) {
				if (j == 2) {
					wVariables.set(j, rotl(wVariables.get(j - 1), 30));
				} else if (j == 0) {
					wVariables.set(j, tList);
				} else {
					wVariables.set(j, wVariables.get(j - 1));
				}
			}
		}

		// Round 2
		for (int i = 20; i < 40; i++) {
			// t := (rotl5(A) + h(B, C, D) + E + X[i] + y2)
			tList = add(
					rotl(wVariables.get(0), 5),
					add(hFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j >= 0; j--) {
				if (j == 2) {
					wVariables.set(j, rotl(wVariables.get(j - 1), 30));
				} else if (j == 0) {
					wVariables.set(j, tList);
				} else {
					wVariables.set(j, wVariables.get(j - 1));
				}
			}
		}

		// Round 3
		for (int i = 40; i < 60; i++) {
			// t := (rotl5(A) + g(B, C, D) + E + X[i] + y3)
			tList = add(
					rotl(wVariables.get(0), 5),
					add(gFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(2)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j >= 0; j--) {
				if (j == 2) {
					wVariables.set(j, rotl(wVariables.get(j - 1), 30));
				} else if (j == 0) {
					wVariables.set(j, tList);
				} else {
					wVariables.set(j, wVariables.get(j - 1));
				}
			}
		}

		// Round 4
		for (int i = 60; i < 80; i++) {
			// t := (rotl5(A) + h(B, C, D) + E + X[i] + y4)
			tList = add(
					rotl(wVariables.get(0), 5),
					add(hFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(wVariables.get(4),
									add(xStorage.get(i), yConstants.get(3)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j >= 0; j--) {
				if (j == 2) {
					wVariables.set(j, rotl(wVariables.get(j - 1), 30));
				} else if (j == 0) {
					wVariables.set(j, tList);
				} else {
					wVariables.set(j, wVariables.get(j - 1));
				}
			}
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

	/**
	 * f(B, C, D) = ((B and C) or (not(B) and D)). Does not modify original
	 * lists.
	 * 
	 * @param inputB
	 * @param inputC
	 * @param inputD
	 * @return f(inputB, inputC, inputD)
	 */
	public List<Gate> fFunction(List<Gate> inputB, List<Gate> inputC,
			List<Gate> inputD) {
		return or(and(inputB, inputC), and(not(inputB), inputD));
	}

	/**
	 * g(B, C, D) = ((B and C) or (B and D) or (C and D)). Does not modify
	 * original lists.
	 * 
	 * @param inputB
	 * @param inputC
	 * @param inputD
	 * @return g(inputB, inputC, inputD)
	 */
	public List<Gate> gFunction(List<Gate> inputB, List<Gate> inputC,
			List<Gate> inputD) {
		return or(and(inputB, inputC),
				or(and(inputB, inputD), and(inputC, inputD)));
	}

	/**
	 * h(B, C, D) = (B xor C xor D). Does not modify original lists.
	 * 
	 * @param inputB
	 * @param inputC
	 * @param inputD
	 * @return h(inputB, inputC, inputD)
	 */
	public List<Gate> hFunction(List<Gate> inputB, List<Gate> inputC,
			List<Gate> inputD) {
		return xor(inputB, xor(inputC, inputD));
	}

	/**
	 * Returns list of gates rotated left given numbers. Does not affect
	 * original list.
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

	/**
	 * Fix input in SHA-1 circuit
	 */
	public void fixInput() {
		// Create message with given length
		int length = 64;
		// Fix padding
		setAndFixValue(inputNodes.get(length), true);
		for (int i = length + 1; i < 448; i++) {
			setAndFixValue(inputNodes.get(i), false);
		}
		// Fix message length padding
		String lengthStr = Integer.toBinaryString(length);
		int strLength = lengthStr.length();
		for (int i = 448; i < 512 - strLength; i++) {
			setAndFixValue(inputNodes.get(i), false);
		}
		for (int i = 512 - strLength; i < 512; i++) {
			setAndFixValue(inputNodes.get(i),
					lengthStr.charAt(i - 512 + strLength) == '1');
		}
		// Fix constants
		List<Boolean> constants = getConstants();
		for (int i = 512; i < 800; i++) {
			setAndFixValue(inputNodes.get(i), constants.get(i - 512));
		}
	}

	public void randomlyFixInput() {
		// Fix input
		for (int i = 0; i < 512; i++) {
			if (getRandBoolean()) {
				setAndFixValue(inputNodes.get(i), getRandBoolean());
			}
		}
		// Fix constants
		List<Boolean> constants = getConstants();
		for (int i = 512; i < 800; i++) {
			setAndFixValue(inputNodes.get(i), constants.get(i - 512));
		}
	}

	public void birthdayAttack() {
		// Number of terms to search 2^n/2 where n ~ 74
		int numTerms = (int) Math.pow(2, 16); // Not 2^37 --> <50% chance
		int messageSpace = (int) Math.pow(2, getMinCutEdges().size());
		int count = 0;

		String[] inputs;
		String minCutValues;

		while (table.size() < messageSpace) {
			count += 1;
			System.out.println("Iteration number " + count);
			// Generate 2^(n/2) random terms out of ~ 2^74 terms
			System.out
					.println("Generating " + numTerms + " random messages...");
			inputs = new String[numTerms];
			for (int i = 0; i < numTerms; i++) {
				inputs[i] = booleanListToString(generateSHA1Input());
			}
			System.out.println("All random messages generated.");
			// Hash all the terms in the term_array
			System.out.println("Hashing all random messages...");
			for (String input : inputs) {
				minCutValues = booleanListToString(getMinCutValues(input));
				String value = table.get(minCutValues);
				if (value != null && !value.equals(input)) {
					System.out
							.println("Collision detected!\n"
									+ binarytoHexString(value)
									+ " --> "
									+ binarytoHexString(booleanListToString(getOutput(value)))
									+ "\n"
									+ binarytoHexString(input)
									+ " --> "
									+ binarytoHexString(booleanListToString(getOutput(input))));
					return;
				} else {
					table.put(minCutValues, input);
				}
			}
			System.out.println("All messages hashed.");
			System.out.println("No collisions found. Trying with " + numTerms
					+ " new random terms.");
		}
		System.out.println("No collisions with given fixed input!!");
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
	public static List<Boolean> generateSHA1Input() {
		return generateSHA1Input(getRandInt(448));
	}

	/**
	 * Generates random input of given size
	 * 
	 * @return input
	 */
	public static List<Boolean> generateSHA1Input(int size) {
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
		for (int i = 0; i < 64 - binaryString.length(); i++) {
			input.add(false);
		}
		for (int i = 0; i < binaryString.length(); i++) {
			input.add(binaryString.charAt(i) == '1');
		}

		input.addAll(getConstants());

		return input;
	}

	/**
	 * Generates input from given string
	 * 
	 * @return input
	 */
	public static List<Boolean> generateSHA1Input(String string) {
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
				input.add(binaryStr.charAt(j) == '1');
			}
		}
		// Padding
		input.add(true);
		for (int i = size + 1; i < 448; i++) {
			input.add(false);
		}
		// 64-bit representation of input length
		binaryStr = Integer.toBinaryString(size);
		for (int i = 0; i < 64 - binaryStr.length(); i++) {
			input.add(false);
		}
		for (int i = 0; i < binaryStr.length(); i++) {
			input.add(binaryStr.charAt(i) == '1');
		}

		input.addAll(getConstants());

		return input;
	}
	
	/**
	 * Generates input from given string
	 * 
	 * @return input
	 */
	public static List<Boolean> padInput(String binaryStr) {
		if (binaryStr.length() > 447) {
			throw new IllegalArgumentException(
					"String longer than 447 bits not supported");
		}
		// Construct valid input from string
		List<Boolean> input = new ArrayList<Boolean>();
		// Number of bits in message
		int size = binaryStr.length();
		// Message
		for (int i = 0; i < size; i++) {
			input.add(binaryStr.charAt(i) == '1');
		}
		// Padding
		input.add(true);
		for (int i = size + 1; i < 448; i++) {
			input.add(false);
		}
		// 64-bit representation of input length
		binaryStr = Integer.toBinaryString(size);
		for (int i = 0; i < 64 - binaryStr.length(); i++) {
			input.add(false);
		}
		for (int i = 0; i < binaryStr.length(); i++) {
			input.add(binaryStr.charAt(i) == '1');
		}

		input.addAll(getConstants());

		return input;
	}

	public static List<Boolean> getConstants() {
		List<Boolean> input = new ArrayList<Boolean>(288);
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
				input.add(h.charAt(i) == '1');
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
				input.add(y.charAt(i) == '1');
			}
		}
		return input;
	}
	
	public static String hexToCharString(String hexStr) {
		StringBuilder sb = new StringBuilder();
		String substring;
		for (int i = 0; i < 224; i++) {
			substring = hexStr.substring(2 * i, 2 * (i + 1));
			char value = (char) Integer.parseInt(substring, 16);
			sb.append(value);
		}
		return sb.toString();
	}

	/**
	 * Creates SHA-1 circuit, gathers output
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Get circuit
		System.out.println("Constructing circuit...");
		SHA1 circuit = new SHA1();
		
		// Construct empty string input
		List<Boolean> input = padInput("00100110100101001000111000100100111011101010110111001101011001101001110011001000110100010111101000001011110100110");

		// Input
		System.out.println("Input:");
		System.out.println(binarytoHexString(booleanListToString(input)));

		// Output
		System.out.println("Output:");
		System.out.println(binarytoHexString(booleanListToString(circuit
				.getOutput(input))));

//		// Fix inputs and simplify circuit
//		System.out.println("Fixing input...");
//		// circuit.fixInput();
//		circuit.randomlyFixInput();
//
//		// Input
//		System.out.println("Input:");
//		System.out.println(binarytoHexString(circuit.generateInput()));
//
//		// Simplify circuit
//		System.out.println("Simplifying circuit...");
//		circuit.simplifyCircuit();
//
//		// Min-cut
//		System.out.println("Calculating min-cut...");
//		System.out.println("The edge set is: " + circuit.getMinCutEdges());
//
//		// Birthday attack
//		System.out.println("Carrying out birthday attack...");
//		circuit.birthdayAttack();
	}

}
