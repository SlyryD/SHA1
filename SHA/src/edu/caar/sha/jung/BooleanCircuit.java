package edu.caar.sha.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Models a boolean circuit as a directed acyclic graph. Nodes correspond to
 * gates and edges correspond to wires
 * 
 * @author Ryan
 */
public class BooleanCircuit extends DirectedSparseGraph<Gate, Edge> {

	// Default serialization id
	private static final long serialVersionUID = 1L;
	// Lists of gates in circuit
	protected List<Gate> inputNodes, outputNodes, notGates, andGates, orGates,
			xorGates, nandGates, norGates, xnorGates;
	// Min cut edges calculated once
	private Set<Edge> minCutEdges;

	/**
	 * Constructs boolean circuit with given edge class and initializes
	 * array-based lists of gates
	 * 
	 * @param edgeClass
	 */
	public BooleanCircuit() {
		super();
		inputNodes = new ArrayList<Gate>();
		outputNodes = new ArrayList<Gate>();
		notGates = new ArrayList<Gate>();
		andGates = new ArrayList<Gate>();
		orGates = new ArrayList<Gate>();
		xorGates = new ArrayList<Gate>();
		nandGates = new ArrayList<Gate>();
		norGates = new ArrayList<Gate>();
		xnorGates = new ArrayList<Gate>();
	}

	/**
	 * Initializes graph of circuit
	 */
	public void initializeGraph() {
		// Add input vertices
		for (int i = 0; i < 3; i++) {
			addVertex(GateFactory.getInputNode());
		}

		// Add output vertices
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getOutputNode());
		}

		// Add not gates
		for (int i = 0; i < 3; i++) {
			addVertex(GateFactory.getNotGate());
		}

		// Add and gates
		for (int i = 0; i < 2; i++) {
			addVertex(GateFactory.getAndGate());
		}

		// Add or gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getOrGate());
		}

		// Add xor gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getXorGate());
		}

		// Add nand gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactory.getNandGate());
		}

		// Add nor gates
		for (int i = 0; i < 0; i++) {
			addVertex(GateFactory.getNorGate());
		}

		// Add xnor gates
		for (int i = 0; i < 0; i++) {
			addVertex(GateFactory.getXnorGate());
		}

		// Add edges to create linking structure
		addEdge(new Edge(), inputNodes.get(0), notGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), notGates.get(0), andGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), andGates.get(0), notGates.get(1), EdgeType.DIRECTED);
		addEdge(new Edge(), notGates.get(1), orGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), orGates.get(0), outputNodes.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(1), andGates.get(1),
				EdgeType.DIRECTED);
		addEdge(new Edge(), andGates.get(1), andGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), andGates.get(1), xorGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), xorGates.get(0), orGates.get(0), EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(1), nandGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), nandGates.get(0), andGates.get(1),
				EdgeType.DIRECTED);
		addEdge(new Edge(), inputNodes.get(2), nandGates.get(0),
				EdgeType.DIRECTED);
		addEdge(new Edge(), nandGates.get(0), notGates.get(2),
				EdgeType.DIRECTED);
		addEdge(new Edge(), notGates.get(2), xorGates.get(0), EdgeType.DIRECTED);
	}

	@Override
	public boolean addVertex(Gate gate) {
		switch (gate.getType()) {
		case INPUT:
			inputNodes.add(gate);
			break;
		case OUTPUT:
			outputNodes.add(gate);
			break;
		case NOT:
			notGates.add(gate);
			break;
		case AND:
			andGates.add(gate);
			break;
		case OR:
			orGates.add(gate);
			break;
		case XOR:
			xorGates.add(gate);
			break;
		case NAND:
			nandGates.add(gate);
			break;
		case NOR:
			norGates.add(gate);
			break;
		case XNOR:
			xnorGates.add(gate);
			break;
		default:
			break;
		}
		return super.addVertex(gate);
	}

	@Override
	public boolean removeVertex(Gate gate) {
		switch (gate.getType()) {
		case INPUT:
			inputNodes.remove(gate);
			break;
		case OUTPUT:
			outputNodes.remove(gate);
			break;
		case NOT:
			notGates.remove(gate);
			break;
		case AND:
			andGates.remove(gate);
			break;
		case OR:
			orGates.remove(gate);
			break;
		case XOR:
			xorGates.remove(gate);
			break;
		case NAND:
			nandGates.remove(gate);
			break;
		case NOR:
			norGates.remove(gate);
			break;
		case XNOR:
			xnorGates.remove(gate);
			break;
		default:
			break;
		}
		return super.removeVertex(gate);
	}

	/**
	 * Evaluates circuit at each gate
	 */
	public void evaluateCircuit() {
		for (Gate gate : outputNodes) {
			gate.setValue(evaluateVertex(gate));
		}
	}

	/**
	 * Recursively evaluates value at each gate in graph
	 * 
	 * @param gate
	 * @return value
	 */
	public boolean evaluateVertex(Gate gate) {
		boolean value;
		Iterator<Gate> parents = getPredecessors(gate).iterator();
		switch (gate.getType()) {
		case INPUT:
			value = gate.getValue();
			break;
		case OUTPUT:
			value = evaluateVertex(parents.next());
			break;
		case NOT:
			value = gate.isEvaluated() ? gate.getValue()
					: !evaluateVertex(parents.next());
			break;
		case AND:
			value = gate.isEvaluated() ? gate.getValue()
					: evaluateVertex(parents.next())
							& evaluateVertex(parents.next());
			break;
		case OR:
			value = gate.isEvaluated() ? gate.getValue()
					: evaluateVertex(parents.next())
							| evaluateVertex(parents.next());
			break;
		case XOR:
			value = gate.isEvaluated() ? gate.getValue()
					: evaluateVertex(parents.next())
							^ evaluateVertex(parents.next());
			break;
		case NAND:
			value = gate.isEvaluated() ? gate.getValue()
					: !(evaluateVertex(parents.next()) & evaluateVertex(parents
							.next()));
			break;
		case NOR:
			value = gate.isEvaluated() ? gate.getValue()
					: !(evaluateVertex(parents.next()) | evaluateVertex(parents
							.next()));
			break;
		case XNOR:
			value = gate.isEvaluated() ? gate.getValue()
					: !(evaluateVertex(parents.next()) ^ evaluateVertex(parents
							.next()));
			break;
		default:
			value = false;
			break;
		}
		gate.setValue(value);
		return value;
	}

	/**
	 * Set input of circuit
	 * 
	 * @param input
	 */
	public void setInput(List<Boolean> input) {
		for (int i = 0; i < input.size(); i++) {
			inputNodes.get(i).setValue(input.get(i));
		}
	}

	/**
	 * Get output if input already set
	 * 
	 * @return output
	 */
	public List<Boolean> getOutput() {
		evaluateCircuit();
		List<Boolean> output = new ArrayList<Boolean>(outputNodes.size());
		for (int i = 0; i < outputNodes.size(); i++) {
			output.add(outputNodes.get(i).getValue());
		}
		return output;
	}

	/**
	 * Set input, evaluate, and return output of circuit
	 * 
	 * @param input
	 * @return output
	 */
	public List<Boolean> getOutput(List<Boolean> input) {
		setInput(input);
		evaluateCircuit();
		List<Boolean> output = new ArrayList<Boolean>(outputNodes.size());
		for (int i = 0; i < outputNodes.size(); i++) {
			output.add(outputNodes.get(i).getValue());
		}
		return output;
	}

	/**
	 * Evaluates circuit at each gate from the min cut
	 */
	public void minCutEvaluateCircuit() {
		int inputSize = inputNodes.size();
		List<Boolean> input1 = new ArrayList<Boolean>(inputSize), input2 = new ArrayList<Boolean>(
				inputSize);
		for (int i = 0; i < inputSize; i++) {
			input1.add(null);
			input2.add(null);
		}
		for (Edge edge : getMinCutEdges()) {
			minCutEvaluateVertex(edge, false, input1, input2);
		}
		System.out.println(booleanListToString(input1) + " --> "
				+ booleanListToString(getOutput(input1)));
		System.out.println(booleanListToString(input2) + " --> "
				+ booleanListToString(getOutput(input2)));
	}

	/**
	 * Evaluates each gate
	 */
	public void minCutEvaluateVertex(Edge edge, boolean value,
			List<Boolean> input1, List<Boolean> input2) {
		Gate source = getSource(edge), dest = getDest(edge);
		int sourceNum = source.getNumber(), destNum = dest.getNumber();
		switch (source.getType()) {
		case SINK:
			input1.set(destNum, value);
			input2.set(destNum, value);
			break;
		case INPUT:
			input1.set(sourceNum, value);
			input2.set(sourceNum, value);
			break;
		case OUTPUT:
			minCutEvaluateVertex(getInEdges(source).iterator().next(), false,
					input1, input2);
			break;
		case NOT:
			minCutEvaluateVertex(getInEdges(source).iterator().next(), !value,
					input1, input2);
			break;
		case AND:
			break;
		case OR:
			break;
		case XOR:
			break;
		case NAND:
			break;
		case NOR:
			break;
		case XNOR:
			break;
		default:
			value = false;
			break;
		}
	}

	/**
	 * Simplify circuit by removing unnecessary gates
	 */
	public void simplifyCircuit() {
		for (Gate gate : getVertices()) {
			if (gate.isEvaluated()) {
				// TODO: Simplify evaluated vertices
			}
		}
	}

	public Set<Edge> calcMinCut() {
		// Add sinks to circuit for sake of min cut
		Gate source = GateFactory.getSink(), sink = GateFactory.getSink();
		addVertex(source);
		addVertex(sink);

		// Adds edges from source to inputs, and from outputs to sink
		for (Gate gate : inputNodes) {
			addEdge(new Edge(), source, gate, EdgeType.DIRECTED);
		}
		for (Gate gate : outputNodes) {
			addEdge(new Edge(), gate, sink, EdgeType.DIRECTED);
		}

		// Transformer from edge to capacity
		Transformer<Edge, Integer> capTransformer = new Transformer<Edge, Integer>() {

			@Override
			public Integer transform(Edge edge) {
				return Edge.CAPACITY;
			}
		};

		// Edge flow map
		Map<Edge, Integer> edgeFlowMap = new HashMap<Edge, Integer>();

		// Edge factory for use by the algorithm
		Factory<Edge> edgeFactory = new Factory<Edge>() {

			@Override
			public Edge create() {
				return new Edge();
			}
		};

		// Max flow algorithm
		@SuppressWarnings({ "rawtypes", "unchecked" })
		EdmondsKarpMaxFlow<Gate, Edge> alg = new EdmondsKarpMaxFlow(this,
				source, sink, capTransformer, edgeFlowMap, edgeFactory);
		alg.evaluate();
		System.out.println("The min cut weight is: " + alg.getMaxFlow());

		// Remove source and sink
		// removeVertex(source);
		// removeVertex(sink);

		return alg.getMinCutEdges();
	}

	/**
	 * Returns list of input nodes
	 * 
	 * @return input nodes
	 */
	public List<Gate> getInputNodes() {
		return inputNodes;
	}

	/**
	 * Returns list of output nodes
	 * 
	 * @return output nodes
	 */
	public List<Gate> getOutputNodes() {
		return outputNodes;
	}

	/**
	 * Returns list of not gates
	 * 
	 * @return not gates
	 */
	public List<Gate> getNotGates() {
		return notGates;
	}

	/**
	 * Returns list of and gates
	 * 
	 * @return and gates
	 */
	public List<Gate> getAndGates() {
		return andGates;
	}

	/**
	 * Returns list of or gates
	 * 
	 * @return or gates
	 */
	public List<Gate> getOrGates() {
		return orGates;
	}

	/**
	 * Returns list of xor gates
	 * 
	 * @return xor gates
	 */
	public List<Gate> getXorGates() {
		return xorGates;
	}

	/**
	 * Returns list of nand gates
	 * 
	 * @return nand gates
	 */
	public List<Gate> getNandGates() {
		return nandGates;
	}

	/**
	 * Returns list of nor gates
	 * 
	 * @return nor gates
	 */
	public List<Gate> getNorGates() {
		return norGates;
	}

	/**
	 * Returns list of xnor gates
	 * 
	 * @return xnor gates
	 */
	public List<Gate> getXnorGates() {
		return xnorGates;
	}

	/**
	 * Returns edges in min cut of circuit
	 * 
	 * @return min cut edges
	 */
	public Set<Edge> getMinCutEdges() {
		if (minCutEdges == null) {
			return (minCutEdges = calcMinCut());
		}
		return minCutEdges;
	}

	public static List<Boolean> intToBooleanList(int number) {
		List<Boolean> list = new ArrayList<Boolean>();
		for (char value : Integer.toBinaryString(number).toCharArray()) {
			list.add(value == '1' ? true : false);
		}
		return list;
	}

	public static String booleanListToString(List<Boolean> list) {
		StringBuilder sb = new StringBuilder();
		for (Boolean value : list) {
			if (value != null) {
				sb.append(value ? "1" : "0");
			}
		}
		return sb.toString();
	}

	public static String binarytoHexString(String binaryStr) {
		StringBuilder sb = new StringBuilder();
		String substring;
		for (int i = 0; i < binaryStr.length() / 8; i++) {
			substring = binaryStr.substring(8 * i, 8 * (i + 1));
			sb.append(Integer.toHexString(Integer.parseInt(substring, 2)));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// Get circuit
		BooleanCircuit circuit = new BooleanCircuit();
		circuit.initializeGraph();

		// Get output
		List<Boolean> values = new ArrayList<Boolean>();
		values.add(true);
		values.add(false);
		values.add(true);
		System.out.println("Input: " + booleanListToString(values));
		System.out.println("Output: "
				+ booleanListToString(circuit.getOutput(values)));

		// Associate DisplayCircuit
		DisplayCircuit circuitDisplay = new DisplayCircuit(circuit);
		circuitDisplay.display();

		// Print edges
		System.out.println(circuit);
	}

}
