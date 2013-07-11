package edu.caar.sha.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
	// Source and sink for use by min cut algorithm
	protected Gate source, sink;
	// Min cut edges calculated once
	private Set<Edge> minCutEdges;
	// Version of circuit
	int version, minCutVersion;

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
		source = GateFactory.getSink();
		sink = GateFactory.getSink();
		minCutVersion = version = 0;
	}

	@Override
	public boolean addVertex(Gate gate) {
		version++;
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
		version++;
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

	@Override
	public boolean addEdge(Edge e, Gate v1, Gate v2, EdgeType edge_type) {
		version++;
		return super.addEdge(e, v1, v2, edge_type);
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
	
	public List<Boolean> getInput() {
		List<Boolean> input = new ArrayList<Boolean>(inputNodes.size());
		for (int i = 0; i < inputNodes.size(); i++) {
			input.add(inputNodes.get(i).getValue());
		}
		return input;
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
		Set<Edge> minCutEdges = getMinCutEdges();
		System.out.println(minCutEdges);
		Gate source, dest;
		for (Edge edge : minCutEdges) {
			source = getSource(edge);
			dest = getDest(edge);
			if (source.getType() == Gate.Type.INPUT) {
				source.setValue(true);
			} else if (dest.getType() == Gate.Type.INPUT) {
				dest.setValue(true);
			}
		}
	}

	private boolean getEdgeValue(Edge edge) {
		Gate source = getSource(edge);
		return source.getType() == Gate.Type.SINK ? getDest(edge).getValue()
				: source.getValue();
	}

	private Gate getOtherSource(Gate source, Gate dest) {
		Gate gate = null;
		for (Iterator<Gate> it = getPredecessors(dest).iterator(); it.hasNext();) {
			gate = it.next();
			if (gate != source) {
				return gate;
			}
		}
		throw new IllegalArgumentException(dest + " does not have other source");
	}

	/**
	 * Simplify circuit by removing unnecessary gates
	 */
	public List<Gate> simplifyCircuit() {
		version++;
		List<Gate> variableInputs = new ArrayList<Gate>();
		for (Gate gate : getInputNodes()) {
			if (gate.isEvaluated()) {
				simplifyVertex(gate, variableInputs);
			}
		}
		return variableInputs;
	}

	public void simplifyVertex(Gate gate, List<Gate> variableInputs) {
		LinkedList<Edge> edges = new LinkedList<Edge>(getOutEdges(gate));
		while (!edges.isEmpty()) {
			Gate dest = getDest(edges.pop());
			Gate otherGate, not;
			Edge newEdge;
			switch (dest.getType()) {
			case OUTPUT:
				dest.setValue(gate.getValue());
				break;
			case NOT:
				dest.setValue(!gate.getValue());
				for (Edge edge : getOutEdges(dest)) {
					edges.push(edge);
				}
				break;
			case AND:
				otherGate = getOtherSource(gate, dest);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, gate.getValue() ? otherGate : gate,
							getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!gate.getValue() ? otherGate : gate, variableInputs);
				break;
			case OR:
				otherGate = getOtherSource(gate, dest);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, gate.getValue() ? gate : otherGate,
							getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!gate.getValue() ? gate : otherGate, variableInputs);
				break;
			case XOR:
				// Gate will depend on other gate, circumvent and remove gate
				otherGate = getOtherSource(gate, dest);
				if (gate.getValue()) {
					not = GateFactory.getNotGate();
					addVertex(not);
					addEdge(new Edge(), otherGate, not, EdgeType.DIRECTED);
					for (Edge edge : getOutEdges(dest)) {
						newEdge = new Edge();
						addEdge(newEdge, not, getDest(edge), EdgeType.DIRECTED);
						edges.push(newEdge);
					}
				} else {
					for (Edge edge : getOutEdges(dest)) {
						newEdge = new Edge();
						addEdge(newEdge, otherGate, getDest(edge),
								EdgeType.DIRECTED);
						edges.push(newEdge);
					}
				}
				removeVertex(dest);
				simplifyUp(gate, variableInputs);
				break;
			case NAND:
				otherGate = getOtherSource(gate, dest);
				not = GateFactory.getNotGate();
				addVertex(not);
				addEdge(new Edge(), gate.getValue() ? otherGate : gate, not,
						EdgeType.DIRECTED);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, not, getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!gate.getValue() ? otherGate : gate, variableInputs);
				break;
			case NOR:
				otherGate = getOtherSource(gate, dest);
				not = GateFactory.getNotGate();
				addVertex(not);
				addEdge(new Edge(), gate.getValue() ? gate : otherGate, not,
						EdgeType.DIRECTED);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, not, getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!gate.getValue() ? gate : otherGate, variableInputs);
				break;
			case XNOR:
				// Gate will depend on other gate, circumvent and remove gate
				otherGate = getOtherSource(gate, dest);
				if (gate.getValue()) {
					for (Edge edge : getOutEdges(dest)) {
						newEdge = new Edge();
						addEdge(newEdge, otherGate, getDest(edge),
								EdgeType.DIRECTED);
						edges.push(newEdge);
					}
				} else {
					not = GateFactory.getNotGate();
					addVertex(not);
					addEdge(new Edge(), otherGate, not, EdgeType.DIRECTED);
					for (Edge edge : getOutEdges(dest)) {
						newEdge = new Edge();
						addEdge(newEdge, not, getDest(edge), EdgeType.DIRECTED);
						edges.push(newEdge);
					}
				}
				removeVertex(dest);
				simplifyUp(gate, variableInputs);
				break;
			default:
				break;
			}
		}
	}

	public void simplifyUp(Gate vertex, List<Gate> variableInputs) {
		if (vertex.getType() == Gate.Type.INPUT) {
			if (getSuccessorCount(vertex) == 0) {
				variableInputs.add(vertex);
			}
		} else {
			List<Gate> predecessors = new ArrayList<Gate>(
					getPredecessors(vertex));
			removeVertex(vertex);
			for (Gate gate : predecessors) {
				simplifyUp(gate, variableInputs);
			}
		}
	}

	public Set<Edge> calcMinCut() {
		// Add sinks to circuit for sake of min cut
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
		minCutVersion = version;
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
		if (minCutVersion != version) {
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
	
	public static List<Boolean> StringToBooleanList(String string) {
		List<Boolean> list = new ArrayList<Boolean>(string.length());
		for (char value : string.toCharArray()) {
			list.add(value == '1' ? true : false);
		}
		return list;
	}

	public static String binarytoHexString(String binaryStr) {
		StringBuilder sb = new StringBuilder();
		String substring;
		for (int i = 0; i < binaryStr.length() / 4; i++) {
			substring = binaryStr.substring(4 * i, 4 * (i + 1));
			sb.append(Integer.toHexString(Integer.parseInt(substring, 2)));
		}
		return sb.toString();
	}
}
