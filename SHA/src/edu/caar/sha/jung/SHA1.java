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

	@Override
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
		for (int i = 0; i < 23025; i++) {
			addVertex(GateFactory.getAndGate());
		}
		andIt = andGates.iterator();

		// Add or gates
		for (int i = 0; i < 11670; i++) {
			addVertex(GateFactory.getOrGate());
		}
		orIt = orGates.iterator();

		// Add xor gates
		for (int i = 0; i < 29179; i++) {
			addVertex(GateFactory.getXorGate());
		}
		xorIt = xorGates.iterator();

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
				addEdge(new Edge(), hValue.get(j), outputNodes.get(32 * i + j),
						EdgeType.DIRECTED);
			}
		}
	}

	public Gate not(Gate input) {
		Gate not = notIt.next();
		addEdge(new Edge(), input, not, EdgeType.DIRECTED);
		return not;
	}

	/**
	 * Computes logical not of input and returns output gates
	 * 
	 * @param input
	 * @return output
	 */
	public List<Gate> not(List<Gate> input) {
		List<Gate> output = new ArrayList<Gate>(input.size());
		for (int i = 0; i < input.size(); i++) {
			output.add(not(input.get(i)));
		}
		return output;
	}

	public Gate and(Gate input1, Gate input2) {
		return binaryOperation(Gate.Type.AND, input1, input2);
	}

	public List<Gate> and(List<Gate> input1, List<Gate> input2) {
		return binaryOperation(Gate.Type.AND, input1, input2);
	}

	public Gate or(Gate input1, Gate input2) {
		return binaryOperation(Gate.Type.OR, input1, input2);
	}

	public List<Gate> or(List<Gate> input1, List<Gate> input2) {
		return binaryOperation(Gate.Type.OR, input1, input2);
	}

	public Gate xor(Gate input1, Gate input2) {
		return binaryOperation(Gate.Type.XOR, input1, input2);
	}

	public List<Gate> xor(List<Gate> input1, List<Gate> input2) {
		return binaryOperation(Gate.Type.XOR, input1, input2);
	}

	private Gate binaryOperation(Gate.Type type, Gate input1, Gate input2) {
		Iterator<Gate> gateIt;
		switch (type) {
		case AND:
			gateIt = andIt;
			break;
		case OR:
			gateIt = orIt;
			break;
		case XOR:
		default:
			gateIt = xorIt;
			break;
		}
		Gate output = gateIt.next();
		addEdge(new Edge(), input1, output, EdgeType.DIRECTED);
		addEdge(new Edge(), input2, output, EdgeType.DIRECTED);
		return output;
	}

	/**
	 * Computes logical binary operation on input and returns output gates
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	private List<Gate> binaryOperation(Gate.Type type, List<Gate> input1,
			List<Gate> input2) {
		if (input1.size() != input2.size()) {
			throw new IllegalArgumentException("Inputs have unequal sizes: "
					+ input1.size() + " != " + input2.size());
		}
		List<Gate> output = new ArrayList<Gate>(input1.size());
		for (int i = 0; i < input1.size(); i++) {
			output.add(binaryOperation(type, input1.get(i), input2.get(i)));
		}
		return output;
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

	public List<Gate> rotl(List<Gate> list, int number) {
		List<Gate> newList = new ArrayList<Gate>(list.subList(number, 32));
		newList.addAll(list.subList(0, number));
		return newList;
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

	// public static void testAdd(SHA1 circuit) {
	// // Add input vertices
	// for (int i = 0; i < 64; i++) {
	// circuit.addVertex(GateFactory.getInputNode());
	// }
	//
	// // Add output vertices
	// for (int i = 0; i < 32; i++) {
	// circuit.addVertex(GateFactory.getOutputNode());
	// }
	//
	// // Add and gates
	// for (int i = 0; i < 61; i++) {
	// circuit.addVertex(GateFactory.getAndGate());
	// }
	// circuit.setAndIt(circuit.getAndGates().iterator());
	//
	// // Add or gates
	// for (int i = 0; i < 30; i++) {
	// circuit.addVertex(GateFactory.getOrGate());
	// }
	// circuit.setOrIt(circuit.getOrGates().iterator());
	//
	// // Add xor gates
	// for (int i = 0; i < 63; i++) {
	// circuit.addVertex(GateFactory.getXorGate());
	// }
	// circuit.setXorIt(circuit.getXorGates().iterator());
	//
	// List<Gate> output = circuit.buildAdder(
	// circuit.getInputNodes().subList(0, 32), circuit.getInputNodes()
	// .subList(32, 64));
	// for (int i = 0; i < output.size(); i++) {
	// circuit.addEdge(new Edge(), output.get(i), circuit.getOutputNodes()
	// .get(i), EdgeType.DIRECTED);
	// }
	//
	// int num1 = 123456789, num2 = 987654321;
	// List<Boolean> input1, input2;
	// // input1 = new ArrayList<Boolean>();
	// // input2 = new ArrayList<Boolean>();
	// // for (char value : "11111111111111111111111111111111".toCharArray()) {
	// // boolean val = value == '1' ? true : false;
	// // input1.add(val);
	// // input2.add(val);
	// // }
	// input1 = BooleanCircuit.intToBooleanList(num1);
	// input2 = BooleanCircuit.intToBooleanList(num2);
	//
	// int size1 = input1.size(), size2 = input2.size();
	// for (int i = 0; i < 32 - size1; i++) {
	// input1.add(0, false);
	// }
	// for (int i = 0; i < 32 - size2; i++) {
	// input2.add(0, false);
	// }
	// for (int i = 0; i < 32; i++) {
	// circuit.getInputNodes().get(i).setValue(input1.get(i));
	// circuit.getInputNodes().get(i + 32).setValue(input2.get(i));
	// }
	//
	// System.out.print(BooleanCircuit.booleanListToString(input1) + " + "
	// + BooleanCircuit.booleanListToString(input2) + " = ");
	// System.out.println(BooleanCircuit.booleanListToString(circuit
	// .getOutput()));
	//
	// System.out.print(Integer.parseInt(
	// BooleanCircuit.booleanListToString(input1), 2)
	// + " + "
	// + Integer.parseInt(BooleanCircuit.booleanListToString(input2),
	// 2) + " = ");
	// System.out.println(Integer.parseInt(
	// BooleanCircuit.booleanListToString(circuit.getOutput()), 2));
	// }

	// public static void testOps(SHA1 circuit, Gate.Type type) {
	// // Add input vertices
	// for (int i = 0; i < 8; i++) {
	// circuit.addVertex(GateFactory.getInputNode());
	// }
	//
	// // Build input
	// List<Boolean> input1 = new ArrayList<Boolean>(4);
	// input1.add(false);
	// input1.add(false);
	// input1.add(true);
	// input1.add(true);
	// List<Boolean> input2 = new ArrayList<Boolean>(4);
	// input2.add(false);
	// input2.add(true);
	// input2.add(false);
	// input2.add(true);
	//
	// for (int i = 0; i < 4; i++) {
	// circuit.getInputNodes().get(i).setValue(input1.get(i));
	// circuit.getInputNodes().get(i + 4).setValue(input2.get(i));
	// }
	//
	// // Add output vertices
	// for (int i = 0; i < 4; i++) {
	// circuit.addVertex(GateFactory.getOutputNode());
	// }
	//
	// List<Gate> output;
	// String typeStr;
	//
	// switch (type) {
	// case AND:
	// // Add and gates
	// for (int i = 0; i < 4; i++) {
	// circuit.addVertex(GateFactory.getAndGate());
	// }
	// circuit.setAndIt(circuit.getAndGates().iterator());
	// output = circuit.and(circuit.getInputNodes().subList(0, 4), circuit
	// .getInputNodes().subList(4, 8));
	// typeStr = "AND";
	// break;
	// case OR:
	// // Add or gates
	// for (int i = 0; i < 4; i++) {
	// circuit.addVertex(GateFactory.getOrGate());
	// }
	// circuit.setOrIt(circuit.getOrGates().iterator());
	// output = circuit.or(circuit.getInputNodes().subList(0, 4), circuit
	// .getInputNodes().subList(4, 8));
	// typeStr = "OR";
	// break;
	// case XOR:
	// default:
	// // Add xor gates
	// for (int i = 0; i < 4; i++) {
	// circuit.addVertex(GateFactory.getXorGate());
	// }
	// circuit.setXorIt(circuit.getXorGates().iterator());
	// output = circuit.xor(circuit.getInputNodes().subList(0, 4), circuit
	// .getInputNodes().subList(4, 8));
	// typeStr = "XOR";
	// break;
	// }
	//
	// // Add output nodes
	// for (int i = 0; i < output.size(); i++) {
	// circuit.addEdge(new Edge(), output.get(i), circuit.getOutputNodes()
	// .get(i), EdgeType.DIRECTED);
	// }
	//
	// System.out.print(BooleanCircuit.booleanListToString(input1) + " "
	// + typeStr + " " + BooleanCircuit.booleanListToString(input2)
	// + " = ");
	// System.out.println(BooleanCircuit.booleanListToString(circuit
	// .getOutput()));
	//
	// System.out.println(circuit.getOutputNodes());
	// }

	// public void setNotIt(Iterator<Gate> notIt) {
	// this.notIt = notIt;
	// }
	//
	// public void setAndIt(Iterator<Gate> andIt) {
	// this.andIt = andIt;
	// }
	//
	// public void setOrIt(Iterator<Gate> orIt) {
	// this.orIt = orIt;
	// }
	//
	// public void setXorIt(Iterator<Gate> xorIt) {
	// this.xorIt = xorIt;
	// }

	/**
	 * Creates and displays adder circuit graph
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Get circuit
		SHA1 circuit = new SHA1();

		// testAdd(circuit);
		// testOps(new SHA1(), Gate.Type.AND);
		// testOps(new SHA1(), Gate.Type.OR);
		// testOps(new SHA1(), Gate.Type.XOR);

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
//		List<Boolean> generatedInput = SHA1.generateInput();
//		System.out.println(BooleanCircuit.booleanListToString(generatedInput));

		// Get output, should be da39a3ee5e6b4b0d3255bfef95601890afd80709
		System.out.println(BooleanCircuit.binarytoHexString(BooleanCircuit
				.booleanListToString(circuit.getOutput(input))));

	}

}
