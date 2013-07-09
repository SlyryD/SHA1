package edu.caar.sha.jung;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models circuit for SHA-1 hashing algorithm
 * 
 * @author Ryan
 */
public class SHA1 extends BooleanCircuit {

	// Serialization ID
	private static final long serialVersionUID = -256141103412704175L;

	// Gate iterators
	private Iterator<Gate> notIt, andIt, orIt, xorIt;

	/**
	 * Constructs adder and initializes circuit graph
	 */
	public SHA1() {
		super();
		initializeGraph();
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		// Add input vertices
		for (int i = 0; i < 25; i++) {
			for (int j = 0; j < 32; j++) {
				addVertex(GateFactory.getInputNode());
			}
		}

		// Temporary storage (X[j] in handbook)
		List<List<Gate>> xStorage = new ArrayList<List<Gate>>(80);
		for (int i = 0; i < 16; i++) {
			xStorage.add(inputNodes.subList(32 * i, 32 * (i + 1)));
		}

		// IV Constants (h1, .., h5)
		List<List<Gate>> hConstants = new ArrayList<List<Gate>>(5);
		for (int i = 16; i < 21; i++) {
			hConstants.add(inputNodes.subList(32 * i, 32 * (i + 1)));
		}

		// Working variables
		List<List<Gate>> wVariables = new ArrayList<List<Gate>>(hConstants);

		// Additive Constants (y1, .., y4)
		List<List<Gate>> yConstants = new ArrayList<List<Gate>>(4);
		for (int i = 21; i < 25; i++) {
			yConstants.add(inputNodes.subList(32 * i, 32 * (i + 1)));
		}

		// Add output vertices
		for (int i = 0; i < 160; i++) {
			addVertex(GateFactory.getOutputNode());
		}

		// Add not gates
		for (int i = 0; i < 640; i++) {
			addVertex(GateFactory.getNotGate());
		}
		notIt = notGates.iterator();

		// Add and gates
		for (int i = 0; i < 5760; i++) {
			addVertex(GateFactory.getAndGate());
		}
		andIt = andGates.iterator();

		// Add or gates
		for (int i = 0; i < 1920; i++) {
			addVertex(GateFactory.getOrGate());
		}
		orIt = orGates.iterator();

		// Add xor gates
		for (int i = 0; i < 6144; i++) {
			addVertex(GateFactory.getXorGate());
		}
		xorIt = xorGates.iterator();

		// Expand 16 words to 80 words
		Gate temp1, temp2;
		for (int i = 16; i < 80; i++) {
			List<Gate> tempList = new ArrayList<Gate>(32);
			for (int j = 0; j < 32; j++) {
				temp1 = xorIt.next();
				addEdge(new Edge(), xStorage.get(i - 3).get(j), temp1,
						EdgeType.DIRECTED);
				addEdge(new Edge(), xStorage.get(i - 8).get(j), temp1,
						EdgeType.DIRECTED);
				temp2 = xorIt.next();
				addEdge(new Edge(), temp1, temp2, EdgeType.DIRECTED);
				addEdge(new Edge(), xStorage.get(i - 14).get(j), temp2,
						EdgeType.DIRECTED);
				temp1 = xorIt.next();
				addEdge(new Edge(), temp2, temp1, EdgeType.DIRECTED);
				addEdge(new Edge(), xStorage.get(i - 16).get(j), temp1,
						EdgeType.DIRECTED);
				tempList.add(temp1);
			}
			// Rotate each X[i] left 1 bit
			xStorage.add(rotl(tempList, 1));
		}

		// Round 1
		for (int i = 0; i < 20; i++) {
			// t := (rotl5(A) + f(B, C, D) + E + X[i] + y1)
			buildAdder(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					buildAdder(
							fFunction(wVariables.get(1), wVariables.get(2),
									wVariables.get(3)),
							buildAdder(
									wVariables.get(4),
									buildAdder(xStorage.get(i),
											yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
		}

		// Round 2
		for (int i = 20; i < 40; i++) {
			// t := (rotl5(A) + h(B, C, D) + E + X[i] + y2)
			buildAdder(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					buildAdder(
							hFunction(wVariables.get(1), wVariables.get(2),
									wVariables.get(3)),
							buildAdder(
									wVariables.get(4),
									buildAdder(xStorage.get(i),
											yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
		}

		// Round 3
		for (int i = 40; i < 60; i++) {
			// t := (rotl5(A) + g(B, C, D) + E + X[i] + y3)
			buildAdder(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					buildAdder(
							gFunction(wVariables.get(1), wVariables.get(2),
									wVariables.get(3)),
							buildAdder(
									wVariables.get(4),
									buildAdder(xStorage.get(i),
											yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
		}

		// Round 4
		for (int i = 60; i < 80; i++) {
			// t := (rotl5(A) + h(B, C, D) + E + X[i] + y4)
			buildAdder(
					rotl(new ArrayList<Gate>(wVariables.get(0)), 5),
					buildAdder(
							hFunction(wVariables.get(1), wVariables.get(2),
									wVariables.get(3)),
							buildAdder(
									wVariables.get(4),
									buildAdder(xStorage.get(i),
											yConstants.get(1)))));
			// (A, B, C, D, E) := (t, A, rotl30(B), C, D)
			for (int j = 4; j > 0; j--) {
				wVariables.set(j, j == 2 ? rotl(wVariables.get(j - 1), 30)
						: wVariables.get(j - 1));
			}
		}

		// Update chaining variables

	}

	public List<Gate> rotl(List<Gate> list, int number) {
		List<Gate> newList = list.subList(number, 32);
		newList.addAll(list.subList(0, number));
		return newList;
	}

	/**
	 * Creates circuit which adds two 32-bit numbers (mod 2^32)
	 * 
	 * @param input1
	 * @param input2
	 */
	public List<Gate> buildAdder(List<Gate> input1, List<Gate> input2) {
		if (input1.size() != 32 || input2.size() != 32) {
			throw new IllegalArgumentException("Input invalid length");
		}
		List<Gate> outputGates = new ArrayList<Gate>(32);
		// Half adder
		Gate output = xorIt.next(), carryover = andIt.next(), xor, and1, and2;
		outputGates.add(output);
		addEdge(new Edge(), input1.get(31), output, EdgeType.DIRECTED);
		addEdge(new Edge(), input2.get(31), output, EdgeType.DIRECTED);
		addEdge(new Edge(), input1.get(31), carryover, EdgeType.DIRECTED);
		addEdge(new Edge(), input2.get(31), carryover, EdgeType.DIRECTED);
		// Full adders
		for (int i = 30; i > 0; i--) {
			xor = xorIt.next();
			output = xorIt.next();
			outputGates.add(output);
			and1 = andIt.next();
			and2 = andIt.next();
			addEdge(new Edge(), input1.get(i), xor, EdgeType.DIRECTED);
			addEdge(new Edge(), input2.get(i), xor, EdgeType.DIRECTED);
			addEdge(new Edge(), input1.get(i), and1, EdgeType.DIRECTED);
			addEdge(new Edge(), input2.get(i), and1, EdgeType.DIRECTED);
			addEdge(new Edge(), xor, output, EdgeType.DIRECTED);
			addEdge(new Edge(), carryover, output, EdgeType.DIRECTED);
			addEdge(new Edge(), xor, and2, EdgeType.DIRECTED);
			addEdge(new Edge(), carryover, and2, EdgeType.DIRECTED);
			carryover = orIt.next();
			addEdge(new Edge(), and2, carryover, EdgeType.DIRECTED);
			addEdge(new Edge(), and1, carryover, EdgeType.DIRECTED);
		}
		// Final adder
		xor = xorIt.next();
		output = xorIt.next();
		outputGates.add(output);
		addEdge(new Edge(), input1.get(0), xor, EdgeType.DIRECTED);
		addEdge(new Edge(), input2.get(0), xor, EdgeType.DIRECTED);
		addEdge(new Edge(), xor, output, EdgeType.DIRECTED);
		addEdge(new Edge(), carryover, output, EdgeType.DIRECTED);
		// Return output gates
		return outputGates;
	}

	/**
	 * f(B, C, D) = ((B and C) or (not(B) and D))
	 * 
	 * @param input1
	 * @param input2
	 * @param input3
	 * @return
	 */
	public List<Gate> fFunction(List<Gate> input1, List<Gate> input2,
			List<Gate> input3) {
		List<Gate> outputGates = new ArrayList<Gate>(32);

		// Return output gates
		return outputGates;
	}

	/**
	 * g(B, C, D) = ((B and C) or (B and D) or (C and D))
	 * 
	 * @param input1
	 * @param input2
	 * @param input3
	 * @return
	 */
	public List<Gate> gFunction(List<Gate> input1, List<Gate> input2,
			List<Gate> input3) {
		List<Gate> outputGates = new ArrayList<Gate>(32);

		// Return output gates
		return outputGates;
	}

	/**
	 * h(B, C, D) = (B xor C xor D)
	 * 
	 * @param input1
	 * @param input2
	 * @param input3
	 * @return
	 */
	public List<Gate> hFunction(List<Gate> input1, List<Gate> input2,
			List<Gate> input3) {
		List<Gate> outputGates = new ArrayList<Gate>(32);

		// Return output gates
		return outputGates;
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public static List<Boolean> generateInput() {
		// Construct valid random input
		List<Boolean> input = new ArrayList<Boolean>();
		// Message
		int size = (int) (Math.random() * 448);
		for (int i = 0; i < size; i++) {
			input.add(((int) (Math.random() * 2)) == 1 ? true : false);
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
		for (char value : binaryString.toCharArray()) {
			input.add(value == '1' ? true : false);
		}
		// Constant input
		String[] constants = new String[] { Integer.toBinaryString(0x67452301),
				Integer.toBinaryString(0xefcdab89),
				Integer.toBinaryString(0x98badcfe),
				Integer.toBinaryString(0x10325476),
				Integer.toBinaryString(0xc3d2e1f0) };
		for (String k : constants) {
			for (int i = 0; i < 32 - k.length(); i++) {
				input.add(false);
			}
			for (char value : k.toCharArray()) {
				input.add(value == '1' ? true : false);
			}
		}
		return input;
	}

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Get circuit
		BooleanCircuit circuit = new SHA1();

		// Construct empty string input
		List<Boolean> input = new ArrayList<Boolean>();
		// Padding
		input.add(true);
		for (int i = 1; i < 448; i++) {
			input.add(false);
		}
		// 64-bit representation of input length
		for (int i = 448; i < 512; i++) {
			input.add(false);
		}
		// Constant input
		String[] constants = new String[] { Integer.toBinaryString(0x67452301),
				Integer.toBinaryString(0xefcdab89),
				Integer.toBinaryString(0x98badcfe),
				Integer.toBinaryString(0x10325476),
				Integer.toBinaryString(0xc3d2e1f0) };
		for (String k : constants) {
			for (int i = 0; i < 32 - k.length(); i++) {
				input.add(false);
			}
			for (char value : k.toCharArray()) {
				input.add(value == '1' ? true : false);
			}
		}

		// Valid randomly generated input
		List<Boolean> generatedInput = SHA1.generateInput();
		System.out.println(BooleanCircuit.booleanListToString(generatedInput));
		System.out.println(generatedInput.size());

		// Get output, should be da39a3ee5e6b4b0d3255bfef95601890afd80709
		System.out.println(circuit.getOutput(input));
	}

}
