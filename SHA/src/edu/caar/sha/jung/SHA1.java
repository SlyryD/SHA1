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

	List<List<Gate>> debugList;

	/**
	 * Constructs SHA-1 circuit graph
	 */
	public SHA1() {
		super();

		debugList = new ArrayList<List<Gate>>();

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
			// TODO: Remove
			for (List<Gate> list : wVariables) {
				debugList.add(list);
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
			// TODO: Remove
			for (List<Gate> list : wVariables) {
				debugList.add(list);
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
			// TODO: Remove
			for (List<Gate> list : wVariables) {
				debugList.add(list);
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
			// TODO: Remove
			for (List<Gate> list : wVariables) {
				debugList.add(list);
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

	public void birthdayAttack() {
		// Number of terms to search 2^n/2 where n = 448
		int numTerms = (int) Math.pow(2, 22); // too many terms
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
			input.add(binaryString.charAt(i) == '1');
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
		for (int i = 0; i < 32; i++) {
			input.add(false);
		}
		for (int i = 0; i < 32 - binaryStr.length(); i++) {
			input.add(false);
		}
		for (int i = 0; i < binaryStr.length(); i++) {
			input.add(binaryStr.charAt(i) == '1');
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

		// Output
		System.out.println("Output:");
		System.out.println(binarytoHexString(booleanListToString(circuit
				.getOutput(input))));

		// Print debug
		System.out.println("\tA\t\tB\t\tC\t\tD\t\tE");
		for (int i = 0; i < 80; i++) {
			System.out.print("t = " + i + ":\t");
			System.out.print(binarytoHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get(5 * i)))) + "\t");
			System.out.print(binarytoHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get(5 * i + 1)))) + "\t");
			System.out.print(binarytoHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get(5 * i + 2)))) + "\t");
			System.out.print(binarytoHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get(5 * i + 3)))) + "\t");
			System.out.print(binarytoHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get(5 * i + 4)))) + "\t");
			System.out.println();
		}

		System.out.println("------ArrayList Test------");
		SHA1 bc = new SHA1();
		bc.setInput(generateInput("abc"));
		// Get constants
		List<Boolean> constants = new ArrayList<Boolean>();
		addConstants(constants);
		List<Gate> listA = new ArrayList<Gate>(32);
		for (int i = 0; i < 32; i++) {
			Gate inputNode = bc.getInputNode();
			bc.setValue(inputNode, constants.get(i));
			listA.add(inputNode);
		}
		List<Gate> listB = new ArrayList<Gate>(32);
		for (int i = 32; i < 64; i++) {
			Gate inputNode = bc.getInputNode();
			bc.setValue(inputNode, constants.get(i));
			listB.add(inputNode);
		}
		List<Gate> listC = new ArrayList<Gate>(32);
		for (int i = 64; i < 96; i++) {
			Gate inputNode = bc.getInputNode();
			bc.setValue(inputNode, constants.get(i));
			listC.add(inputNode);
		}
		List<Gate> listD = new ArrayList<Gate>(32);
		for (int i = 96; i < 128; i++) {
			Gate inputNode = bc.getInputNode();
			bc.setValue(inputNode, constants.get(i));
			listD.add(inputNode);
		}
		List<Gate> listE = new ArrayList<Gate>(32);
		for (int i = 128; i < 160; i++) {
			Gate inputNode = bc.getInputNode();
			bc.setValue(inputNode, constants.get(i));
			listE.add(inputNode);
		}
		System.out.println(listA);
		System.out.println(listB);
		System.out.println(listC);
		System.out.println(listD);
		System.out.println(listE);
		System.out.println("A:\t\t"
				+ booleanListToString(bc.getGateValues(listA)));
		System.out.println("B:\t\t"
				+ booleanListToString(bc.getGateValues(listB)));
		System.out.println("C:\t\t"
				+ booleanListToString(bc.getGateValues(listC)));
		System.out.println("D:\t\t"
				+ booleanListToString(bc.getGateValues(listD)));
		System.out.println("E:\t\t"
				+ booleanListToString(bc.getGateValues(listE)));
		List<Gate> rotlGates = bc.rotl(listA, 5);
		System.out.println("rotl(A, 5):\t"
				+ booleanListToString(bc.getGateValues(rotlGates)));
		List<Gate> fGates = bc.fFunction(listB, listC, listD);
		bc.evaluateCircuitToGates(fGates);
		// System.out.println("f(B, C, D):\t"
		// + booleanListToString(bc.getGateValues(fGates)));
		// System.out.println("E:\t\t"
		// + booleanListToString(bc.getGateValues(listE)));
		List<Gate> x0 = bc.getInputNodes().subList(0, 32);
		// System.out.println("X[0]:\t\t"
		// + booleanListToString(bc.getGateValues(x0)));
		List<Gate> y1 = bc.getInputNodes().subList(672, 704);
		// System.out.println("y1:\t\t"
		// + booleanListToString(bc.getGateValues(y1)));
		List<Gate> addGates = bc.add(x0, y1);
		bc.evaluateCircuitToGates(addGates);
		// System.out.println("x[0] + y1:\t" +
		// booleanListToString(bc.getGateValues(addGates)));
		addGates = bc.add(listE, addGates);
		bc.evaluateCircuitToGates(addGates);
		// System.out.println("E + x[0] + y1:\t" +
		// booleanListToString(bc.getGateValues(addGates)));
		addGates = bc.add(fGates, addGates);
		bc.evaluateCircuitToGates(addGates);
		System.out.println("f + E + x0 + y1:"
				+ booleanListToString(bc.getGateValues(addGates)));
		addGates = bc.add(rotlGates, addGates);
		bc.evaluateCircuitToGates(addGates);
		System.out.println("rotl5+f+E+x0+y1:"
				+ booleanListToString(bc.getGateValues(addGates)));
		System.out.println("rotl5+f+E+x0+y1:"
				+ Integer.toHexString(Integer.parseInt(
						booleanListToString(bc.getGateValues(addGates)), 2)));
		// List<Gate> gates = bc.hFunction(listA, listB, listC);
		// bc.evaluateCircuitToGates(gates);
		// System.out.println(booleanListToString(bc.getGateValues(gates)));
		// System.out.println(listA);
		// System.out.println(listB);
		// System.out.println(listC);
		// System.out.println(listD);
		// System.out.println(listE);
	}

}
