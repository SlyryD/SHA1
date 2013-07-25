package edu.caar.md4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.caar.circuit.BooleanCircuit;
import edu.caar.circuit.Edge;
import edu.caar.circuit.Gate;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models circuit for MD4 hash algorithm
 * 
 * @author Ryan
 */
public class MD4 extends BooleanCircuit {

	// Serialization ID
	private static final long serialVersionUID = -5058951136996351070L;

	// Hash table for birthday attack
	HashMap<String, String> table;

	// TODO: Debug list
	List<List<Gate>> debugList = new ArrayList<List<Gate>>();

	/**
	 * Constructs MD4 circuit graph
	 */
	public MD4() {
		// Initializes circuit
		super();

		// Adds vertices and edges to graph
		initializeGraph();

		// Initializes hash table
		table = new HashMap<String, String>();
	}

	/**
	 * Initializes graph of MD4 circuit
	 */
	public void initializeGraph() {
		// Order for accessing source words (z[j] in handbook)
		int[] zArray = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11,
				15, 0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15 };

		// Number of bit positions for left shifts (s[j])
		int[] sArray = new int[] { 3, 7, 11, 19, 3, 7, 11, 19, 3, 7, 11, 19, 3,
				7, 11, 19, 3, 5, 9, 13, 3, 5, 9, 13, 3, 5, 9, 13, 3, 5, 9, 13,
				3, 9, 11, 15, 3, 9, 11, 15, 3, 9, 11, 15, 3, 9, 11, 15 };

		// Temporary storage (X[j])
		List<Gate> xEntry;
		List<List<Gate>> xStorage = new ArrayList<List<Gate>>(16);
		for (int i = 0; i < 16; i++) {
			xEntry = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				xEntry.add(getInputNode());
			}
			xStorage.add(xEntry);
		}

		// IV Constants (h1, h2, h3, h4)
		List<Gate> hEntry;
		List<List<Gate>> hConstants = new ArrayList<List<Gate>>(4);
		for (int i = 0; i < 4; i++) {
			hEntry = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				hEntry.add(getInputNode());
			}
			hConstants.add(hEntry);
		}

		// Working variables (A, B, C, D)
		List<List<Gate>> wVariables = new ArrayList<List<Gate>>(hConstants);

		// Additive Constants (y1, y2, y3)
		List<Gate> yEntry;
		List<List<Gate>> yConstants = new ArrayList<List<Gate>>(3);
		for (int i = 0; i < 3; i++) {
			yEntry = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				yEntry.add(getInputNode());
			}
			yConstants.add(yEntry);
		}

		// Temporary list of 32 gates
		List<Gate> tList;

		// TODO: Debug
		for (int i = 0; i < 4; i++) {
			debugList.add(wVariables.get(i));
		}

		// Round 1
		for (int i = 0; i < 16; i++) {
			// t := (A + f(B, C, D) + X[z[j]] + y1)
			tList = add(
					wVariables.get(0),
					add(fFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(xStorage.get(zArray[i]), yConstants.get(0))));
			// (A, B, C, D) := (D, rotls(t), B, C)
			wVariables.set(0, wVariables.get(3));
			wVariables.set(3, wVariables.get(2));
			wVariables.set(2, wVariables.get(1));
			wVariables.set(1, rotl(tList, sArray[i]));

			// TODO: Debug
			for (int j = 0; j < 4; j++) {
				debugList.add(wVariables.get(j));
			}
		}

		// Round 2
		for (int i = 16; i < 32; i++) {
			// t := (A + g(B, C, D) + X[z[j]] + y2)
			tList = add(
					wVariables.get(0),
					add(gFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(xStorage.get(zArray[i]), yConstants.get(1))));
			// (A, B, C, D) := (D, rotls(t), B, C)
			wVariables.set(0, wVariables.get(3));
			wVariables.set(3, wVariables.get(2));
			wVariables.set(2, wVariables.get(1));
			wVariables.set(1, rotl(tList, sArray[i]));

			// TODO: Debug
			for (int j = 0; j < 4; j++) {
				debugList.add(wVariables.get(j));
			}
		}

		// Round 3
		for (int i = 32; i < 48; i++) {
			// t := (A + h(B, C, D) + X[z[j]] + y3)
			tList = add(
					wVariables.get(0),
					add(hFunction(wVariables.get(1), wVariables.get(2),
							wVariables.get(3)),
							add(xStorage.get(zArray[i]), yConstants.get(2))));
			// (A, B, C, D) := (D, rotls(t), B, C)
			wVariables.set(0, wVariables.get(3));
			wVariables.set(3, wVariables.get(2));
			wVariables.set(2, wVariables.get(1));
			wVariables.set(1, rotl(tList, sArray[i]));

			// TODO: Debug
			for (int j = 0; j < 4; j++) {
				debugList.add(wVariables.get(j));
			}
		}

		// Update chaining variables
		List<Gate> hValue;
		for (int i = 0; i < 4; i++) {
			hValue = changeEndianess(add(hConstants.get(i), wVariables.get(i)));
			for (int j = 0; j < 32; j++) {
				addEdge(new Edge(), hValue.get(j), getOutputNode(),
						EdgeType.DIRECTED);
			}
		}

		// Set y constants and simplify circuit (builds constants into circuit)
		List<Boolean> constants = getYConstants();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 32; j++) {
				setAndFixValue(yConstants.get(i).get(j),
						constants.get((i << 5) + j));
			}
		}
		simplifyCircuit();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 32; j++) {
				removeVertex(yConstants.get(i).get(j));
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

	// /**
	// * Creates circuit which adds two 32-bit numbers (mod 2^32). Does not
	// modify
	// * original lists.
	// *
	// * @param input1
	// * @param input2
	// */
	// // TODO: Deal with endianess
	// public List<Gate> add(List<Gate> input1, List<Gate> input2) {
	// // Inputs must be 32-bits long
	// if (input1.size() != 32 || input2.size() != 32) {
	// throw new IllegalArgumentException("Input invalid length");
	// }
	//
	// // Output gates of adder circuit
	// List<Gate> outputGates = new ArrayList<Gate>(32);
	//
	// // Half adder
	// Gate xor;
	// outputGates.add(xor(input1.get(7), input2.get(7)));
	// Gate carryover = and(input1.get(7), input2.get(7));
	//
	// // Full adders
	// int index;
	// for (int i = 0; i < 4; i++) {
	// for (int j = 8; j > 0; j--) {
	// index = (i << 3) + j - 1;
	// xor = xor(input1.get(index), input2.get(index));
	// outputGates.add(index, xor(xor, carryover));
	// carryover = or(and(input1.get(index), input2.get(index)),
	// and(xor, carryover));
	// }
	// }
	//
	// // Final adder
	// outputGates.add(24, xor(xor(input1.get(0), input2.get(0)), carryover));
	//
	// // Return output gates
	// return outputGates;
	// }

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
	 * Returns gates in reverse byte-endian order
	 * 
	 * @param gates
	 * @return gates
	 */
	private List<Gate> changeEndianess(List<Gate> gates) {
		List<Gate> newList = new ArrayList<Gate>(32);
		for (int i = 4; i > 0; i--) {
			newList.addAll(gates.subList((i - 1) << 3, i << 3));
		}
		return newList;
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
		List<Gate> endianList = changeEndianess(list);
		List<Gate> newList = endianList.subList(number, 32);
		newList.addAll(endianList.subList(0, number));
		return changeEndianess(newList);
	}

	/**
	 * Fix input in MD4 circuit
	 */
	public void fixInput(BufferedWriter out) throws IOException {
		// Reset values and fixed gates
		resetAllGates();

		// Message has given length
		int length = 64;
		for (int i = 0; i < length; i++) {
			out.write("-1,");
		}

		// Fix padding
		setAndFixValue(inputNodes.get(length), true);
		out.write("1,");
		for (int i = length + 1; i < 448; i++) {
			setAndFixValue(inputNodes.get(i), false);
			out.write("0,");
		}

		// Fix message length padding
		String lengthStr = getLengthRep(length);
		for (int i = 0; i < 64; i++) {
			if (lengthStr.charAt(i) == '1') {
				setAndFixValue(inputNodes.get(i + 448), true);
				out.write("1,");
			} else {
				setAndFixValue(inputNodes.get(i + 448), false);
				out.write("0,");
			}
		}

		// Fix constants
		List<Boolean> constants = getHConstants();
		for (int i = 0; i < 160; i++) {
			setAndFixValue(inputNodes.get(i + 512), constants.get(i));
			out.write(constants.get(i) ? "1," : "0,");
		}
		out.write(672 - length + ",");
	}

	public void randomlyFixInput(BufferedWriter out) throws IOException {
		resetAllGates();

		// // Write to file
		// for (int i = 0; i < 672; i++) {
		// out.write(inputNodes.get(i).toString() + ",");
		// }
		// out.write("Num Fixed");
		// out.write("Min-cut Weight\n");

		// Fix input
		int number = 500;
		Map<Integer, Boolean> ints = BooleanCircuit.getRandInts(number, 672);
		boolean value;
		for (int i = 0; i < 672; i++) {
			if (ints.containsKey(i)) {
				value = getRandBoolean();
				setAndFixValue(inputNodes.get(i), value);
				out.write(value ? "1," : "0,");
			} else {
				out.write("-1,");
			}
		}
		out.write(number + ",");
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
				inputs[i] = booleanListToString(generateMD4Input());
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
									+ binaryToHexString(value)
									+ " --> "
									+ binaryToHexString(booleanListToString(getOutput(value)))
									+ "\n"
									+ binaryToHexString(input)
									+ " --> "
									+ binaryToHexString(booleanListToString(getOutput(input))));
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

	/**
	 * Generates random input of random size
	 * 
	 * @return input
	 */
	public static List<Boolean> generateMD4Input() {
		return generateMD4Input(getRandInt(448));
	}

	/**
	 * Generates random input of given size
	 * 
	 * @return input
	 */
	public static List<Boolean> generateMD4Input(int size) {
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
		String lengthStr = getLengthRep(size);
		for (int i = 0; i < 64; i++) {
			input.add(lengthStr.charAt(i) == '1');
		}

		// Add h constants
		input.addAll(getHConstants());

		return input;
	}

	/**
	 * Generates input from given string
	 * 
	 * @return input
	 */
	public static List<Boolean> generateMD4Input(String string) {
		// Does not support 448-bit or longer strings
		if (string.length() > 55) {
			throw new IllegalArgumentException(
					"String longer than 447 bits not supported");
		}

		// Construct valid input from string
		List<Boolean> input = new ArrayList<Boolean>();

		// Number of bits in message
		int size = string.length() << 3;

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
		String lengthStr = getLengthRep(size);
		for (int i = 0; i < 64; i++) {
			input.add(lengthStr.charAt(i) == '1');
		}

		// Add h constants
		input.addAll(getHConstants());

		return input;
	}

	/**
	 * Generates input from given string
	 * 
	 * @return input
	 */
	public static List<Boolean> padInput(String binaryStr) {
		// Does not support 448-bit or longer strings
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
		String lengthStr = getLengthRep(size);
		for (int i = 0; i < 64; i++) {
			input.add(lengthStr.charAt(i) == '1');
		}

		// Add h constants
		input.addAll(getHConstants());

		return input;
	}

	// public static List<Boolean> getHConstants() {
	// List<Boolean> input = new ArrayList<Boolean>(128);
	// // IV constants
	// String[] hConstants = new String[] {
	// Integer.toBinaryString(0x01234567),
	// Integer.toBinaryString(0x89abcdef),
	// Integer.toBinaryString(0xfedcba98),
	// Integer.toBinaryString(0x76543210) };
	// for (String h : hConstants) {
	// for (int i = 0; i < 32 - h.length(); i++) {
	// input.add(false);
	// }
	// for (int i = 0; i < h.length(); i++) {
	// input.add(h.charAt(i) == '1');
	// }
	// }
	// return input;
	// }

	// public static List<Boolean> getYConstants() {
	// List<Boolean> input = new ArrayList<Boolean>(96);
	// // Additive constants
	// String[] yConstants = new String[] { Integer.toBinaryString(0),
	// Integer.toBinaryString(0x999728a5),
	// Integer.toBinaryString(0x1abe9de6) };
	// for (String y : yConstants) {
	// for (int i = 0; i < 32 - y.length(); i++) {
	// input.add(false);
	// }
	// for (int i = 0; i < y.length(); i++) {
	// input.add(y.charAt(i) == '1');
	// }
	// }
	// return input;
	// }

	/**
	 * Returns list of booleans corresponding to bits of h constants
	 * 
	 * @return hConstants
	 */
	public static List<Boolean> getHConstants() {
		List<Boolean> input = new ArrayList<Boolean>(160);
		// IV constants
		String[] hConstants = new String[] {
				Integer.toBinaryString(0x67452301),
				Integer.toBinaryString(0xefcdab89),
				Integer.toBinaryString(0x98badcfe),
				Integer.toBinaryString(0x10325476) };
		for (String h : hConstants) {
			for (int i = 0; i < 32 - h.length(); i++) {
				input.add(false);
			}
			for (int i = 0; i < h.length(); i++) {
				input.add(h.charAt(i) == '1');
			}
		}
		return input;
	}

	/**
	 * Returns list of booleans corresponding to bits of y constants
	 * 
	 * @return yConstants
	 */
	public static List<Boolean> getYConstants() {
		List<Boolean> input = new ArrayList<Boolean>(128);
		// Additive constants
		String[] yConstants = new String[] { Integer.toBinaryString(0),
				Integer.toBinaryString(0x5a827999),
				Integer.toBinaryString(0x6ed9eba1) };
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

	/**
	 * Returns little-byte-endian representation of length
	 * 
	 * @param length
	 * @return lengthString
	 */
	public static String getLengthRep(int length) {
		String binaryStr = Integer.toBinaryString(Integer.reverseBytes(length));
		StringBuilder sb = new StringBuilder(binaryStr);
		for (int i = 0; i < 64 - binaryStr.length(); i++) {
			sb.append('0');
		}
		return sb.toString();
	}

	/**
	 * Returns hex representation of binary string (little-endian)
	 * 
	 * @param binaryStr
	 * @return hexString
	 */
	public static String binaryToHexString(String binaryStr) {
		StringBuilder sb = new StringBuilder();
		String substring;
		for (int i = 0; i < binaryStr.length() >> 2; i++) {
			substring = binaryStr.substring(i << 2, (i + 1) << 2);
			sb.append(Integer.toHexString(Integer.parseInt(substring, 2)));
		}
		return sb.toString();
	}

	/**
	 * Creates MD4 circuit, gathers output
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Get circuit
		System.out.println("Constructing circuit...");
		MD4 circuit = new MD4();

		// Construct empty string input
		List<Boolean> input = generateMD4Input("abc");

		// Input
		System.out.println("Input:");
		System.out.println(binaryToHexString(booleanListToString(input)));

		// Output
		System.out.println("Output:");
		System.out.println(binaryToHexString(booleanListToString(circuit
				.getOutput(input))));

		System.out.println("Step\tA\t\tB\t\tC\t\tD\t\t");
		for (int i = 0; i < circuit.debugList.size() >> 2; i++) {
			System.out.print(i + "\t");
			System.out.print(binaryToHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get(i << 2)))) + "\t");
			System.out.print(binaryToHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get((i << 2) + 1)))) + "\t");
			System.out.print(binaryToHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get((i << 2) + 2)))) + "\t");
			System.out.println(binaryToHexString(booleanListToString(circuit
					.getGateValues(circuit.debugList.get((i << 2) + 3)))));
		}

		// // Comma delineated output
		// try {
		// // Create file and write stream
		// FileWriter fstream = new FileWriter("minCutData.csv", true);
		// BufferedWriter out = new BufferedWriter(fstream);
		//
		// // Fix inputs and simplify circuit
		// System.out.println("Fixing input...");
		// circuit.fixInput(out);
		// // circuit.randomlyFixInput(out);
		//
		// // Simplify circuit
		// System.out.println("Simplifying circuit...");
		// List<Gate> variableInputs = circuit.simplifyCircuit();
		//
		// // Print collisions
		// // List<String> inputs = new ArrayList<String>((int) Math.pow(2,
		// // variableInputs.size()));
		// // circuit.generateInputs(variableInputs, inputs);
		// // List<String> outputs = new ArrayList<String>((int)
		// // Math.pow(2,
		// // variableInputs.size()));
		// // for (String input : inputs) {
		// // outputs.add(booleanListToString(circuit.getOutput(input)));
		// // }
		// // System.out.println("Inputs with collisions: " + inputs +
		// // " --> "
		// // + outputs);
		//
		// // System.out
		// // .println(binarytoHexString(booleanListToString(circuit
		// // .getOutput(stringToBooleanList(input)))));
		//
		// // Min-cut
		// System.out.println("Calculating min-cut...");
		// System.out.println("The edge set is: " + circuit.getMinCutEdges());
		// out.write(circuit.getMinCutEdges().size() + ",");
		// out.write(variableInputs.toString().replace(',', ' ') + "\n");
		//
		// // Close stream
		// out.close();
		// } catch (Exception e) {
		// System.err.println("Error: " + e.getMessage());
		// }
		//
		// // // Birthday attack
		// // System.out.println("Carrying out birthday attack...");
		// // circuit.birthdayAttack();
	}

}
