package edu.caar.sha.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

	// Serialization ID
	private static final long serialVersionUID = 7030888650603749395L;
	// Random generator
	private static Random random = new Random();
	// Lists of gates in circuit
	protected List<Gate> inputNodes, outputNodes, notGates, andGates, orGates,
			xorGates, nandGates, norGates, xnorGates;
	// Map of fixed gate values
	protected Map<Gate, Boolean> values, fixed;
	// Source and sink for use by min cut algorithm
	protected Gate source, sink;
	// Min cut edges calculated once
	private Set<Edge> minCutEdges;
	// Version of circuit
	int version, minCutVersion;

	/* ------------------------------ CONSTRUCTOR ------------------------------ */

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
		values = new HashMap<Gate, Boolean>();
		fixed = new HashMap<Gate, Boolean>();
		source = GateFactory.getSink();
		sink = GateFactory.getSink();
		minCutVersion = version = 0;
	}

	/* ------------------ ADD/REMOVE VERTEX AND EDGE METHODS ------------------ */

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

	/* --------------------- GET/SET INPUT/OUTPUT METHODS --------------------- */
	
	/**
	 * Generates random input with fixed values for circuit
	 * 
	 * @param circuit
	 * @return input
	 */
	public String generateInput() {
		StringBuilder sb = new StringBuilder();
		for (Gate gate : inputNodes) {
			if (fixed.containsKey(gate)) {
				sb.append(values.get(gate) ? "1" : "0");
			} else {
				sb.append(getRandBoolean() ? "1" : "0");
			}
		}
		return sb.toString();
	}

	/**
	 * Set input of circuit
	 * 
	 * @param input
	 */
	public void setInput(List<Boolean> input) {
		for (int i = 0; i < inputNodes.size(); i++) {
			values.put(inputNodes.get(i), input.get(i));
		}
	}

	/**
	 * Evaluates circuit at each gate from the min cut
	 */
	public void minCutSetInput() {
		Set<Edge> minCutEdges = getMinCutEdges();
		System.out.println(minCutEdges);
		Gate source, dest;

		fixed.clear();
		resetValues();
		for (Edge edge : minCutEdges) {
			source = getSource(edge);
			dest = getDest(edge);
			if (source.getType() == Gate.Type.INPUT) {
				values.put(source, getRandBoolean());
				fixed.put(source, true);
			} else if (dest.getType() == Gate.Type.INPUT) {
				values.put(dest, getRandBoolean());
				fixed.put(dest, true);
			}
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
			output.add(values.get(outputNodes.get(i)));
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
		return getOutput();
	}

	/**
	 * Get output from string input
	 * 
	 * @param input
	 * @return output
	 */
	public List<Boolean> getOutput(String input) {
		return getOutput(stringToBooleanList(input));
	}

	/* -------------------- VALUES AND FIXED GATES METHODS -------------------- */

	/**
	 * Sets value of gate
	 * 
	 * @param gate
	 * @param value
	 */
	public void setValue(Gate gate, boolean value) {
		values.put(gate, value);
	}

	/**
	 * Resets values of gates that are not fixed
	 */
	public void resetValues() {
		Map<Gate, Boolean> newValues = new HashMap<Gate, Boolean>();
		for (Gate gate : fixed.keySet()) {
			newValues.put(gate, values.get(gate));
		}
		values = newValues;
	}

	/**
	 * Resets fixed gates
	 */
	public void resetFixed() {
		fixed.clear();
	}

	/**
	 * Resets all gates including fixed gates
	 */
	public void resetAllGates() {
		resetFixed();
		resetValues();
	}

	/* ------------------- METHODS FOR CONSTRUCTING CIRCUIT ------------------- */

	/**
	 * Connects and returns not gate
	 * 
	 * @param input
	 * @return output
	 */
	public Gate not(Gate input) {
		Gate output = getNotGate();
		addEdge(new Edge(), input, output, EdgeType.DIRECTED);
		return output;
	}

	/**
	 * Connects and returns not gates
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

	/**
	 * Connects and returns and gate
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	public Gate and(Gate input1, Gate input2) {
		return binaryOperation(Gate.Type.AND, input1, input2);
	}

	/**
	 * Connects and returns and gates
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	public List<Gate> and(List<Gate> input1, List<Gate> input2) {
		return binaryOperation(Gate.Type.AND, input1, input2);
	}

	/**
	 * Connects and returns or gate
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	public Gate or(Gate input1, Gate input2) {
		return binaryOperation(Gate.Type.OR, input1, input2);
	}

	/**
	 * Connects and returns or gates
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	public List<Gate> or(List<Gate> input1, List<Gate> input2) {
		return binaryOperation(Gate.Type.OR, input1, input2);
	}

	/**
	 * Connects and returns xor gate
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	public Gate xor(Gate input1, Gate input2) {
		return binaryOperation(Gate.Type.XOR, input1, input2);
	}

	/**
	 * Connects and returns xor gates
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	public List<Gate> xor(List<Gate> input1, List<Gate> input2) {
		return binaryOperation(Gate.Type.XOR, input1, input2);
	}

	/**
	 * Connects and returns gate of given type
	 * 
	 * @param input1
	 * @param input2
	 * @return output
	 */
	private Gate binaryOperation(Gate.Type type, Gate input1, Gate input2) {
		Gate output;
		switch (type) {
		case AND:
			output = getAndGate();
			break;
		case OR:
			output = getOrGate();
			break;
		case XOR:
		default:
			output = getXorGate();
			break;
		}
		addEdge(new Edge(), input1, output, EdgeType.DIRECTED);
		addEdge(new Edge(), input2, output, EdgeType.DIRECTED);
		return output;
	}

	/**
	 * Connects and returns gates of given type
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

	/* -------------------- METHODS FOR EVALUATING CIRCUIT -------------------- */

	/**
	 * Evaluates circuit at each gate
	 */
	public void evaluateCircuit() {
		for (Gate gate : outputNodes) {
			values.put(gate, evaluateVertex(gate));
		}
	}

	/**
	 * Evaluates circuit up to min cut edges
	 */
	public void minCutEvaluateCircuit() {
		Gate source;
		for (Edge edge : getMinCutEdges()) {
			source = getSource(edge);
			values.put(source, evaluateVertex(source));
		}
	}

	/**
	 * Evaluates circuit up to given gates
	 */
	public void evaluateCircuitToGates(List<Gate> gates) {
		for (Gate gate : gates) {
			values.put(gate, evaluateVertex(gate));
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
			value = values.get(gate);
			break;
		case OUTPUT:
			value = evaluateVertex(parents.next());
			break;
		case NOT:
			value = values.containsKey(gate) ? values.get(gate)
					: !evaluateVertex(parents.next());
			break;
		case AND:
			value = values.containsKey(gate) ? values.get(gate)
					: evaluateVertex(parents.next())
							& evaluateVertex(parents.next());
			break;
		case OR:
			value = values.containsKey(gate) ? values.get(gate)
					: evaluateVertex(parents.next())
							| evaluateVertex(parents.next());
			break;
		case XOR:
			value = values.containsKey(gate) ? values.get(gate)
					: evaluateVertex(parents.next())
							^ evaluateVertex(parents.next());
			break;
		case NAND:
			value = values.containsKey(gate) ? values.get(gate)
					: !(evaluateVertex(parents.next()) & evaluateVertex(parents
							.next()));
			break;
		case NOR:
			value = values.containsKey(gate) ? values.get(gate)
					: !(evaluateVertex(parents.next()) | evaluateVertex(parents
							.next()));
			break;
		case XNOR:
			value = values.containsKey(gate) ? values.get(gate)
					: !(evaluateVertex(parents.next()) ^ evaluateVertex(parents
							.next()));
			break;
		default:
			value = false;
			break;
		}
		values.put(gate, value);
		return value;
	}

	/* -------------------- METHODS FOR GETTING GATE VALUES -------------------- */

	/**
	 * Get values of given gates
	 * 
	 * @param gates
	 * @return values
	 */
	public List<Boolean> getGateValues(List<Gate> gates) {
		List<Boolean> list = new ArrayList<Boolean>();
		for (Gate gate : gates) {
			list.add(values.get(gate));
		}
		return list;
	}

	/**
	 * Get value at edge
	 * 
	 * @param edge
	 * @return value
	 */
	private boolean getEdgeValue(Edge edge) {
		Gate source = getSource(edge);
		// Edge not in graph if source is null
		if (source == null) {
			throw new IllegalArgumentException("Edge " + edge.toString()
					+ getEndpoints(edge) + " not in graph");
		}
		return source.getType() == Gate.Type.SINK ? values.get(getDest(edge))
				: values.get(source);
	}

	/**
	 * Evaluate circuit and get values at min cut edges
	 * 
	 * @return values
	 */
	public List<Boolean> getMinCutValues() {
		minCutEvaluateCircuit();
		List<Boolean> values = new ArrayList<Boolean>(getMinCutEdges().size());
		for (Iterator<Edge> it = getMinCutEdges().iterator(); it.hasNext();) {
			values.add(getEdgeValue(it.next()));
		}
		return values;
	}

	/**
	 * Set input and get values at min cut edges
	 * 
	 * @param input
	 * @return values
	 */
	public List<Boolean> getMinCutValues(List<Boolean> input) {
		setInput(input);
		return getMinCutValues();
	}

	/**
	 * Set input and get values at min cut edges
	 * 
	 * @param input
	 * @return values
	 */
	public List<Boolean> getMinCutValues(String input) {
		return getMinCutValues(stringToBooleanList(input));
	}

	/* -------------------- MIN CUT ALGORITHM METHOD -------------------- */

	/**
	 * Calculates min cut of circuit
	 * 
	 * @return min cut edges
	 */
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
		// removeVertex(source);
		// removeVertex(sink);
		minCutVersion = version;
		return alg.getMinCutEdges();
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

	/* -------------------- METHODS FOR SIMPLIFYING CIRCUIT -------------------- */

	/**
	 * Returns other predecessor of dest
	 * 
	 * @param source
	 * @param dest
	 * @return other source
	 */
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
	 * 
	 * @return inputs that can be toggled w/o affecting circuit output
	 */
	public List<Gate> simplifyCircuit() {
		List<Gate> variableInputs = new ArrayList<Gate>();
		for (Gate gate : getInputNodes()) {
			if (values.containsKey(gate)) {
				simplifyVertex(gate, variableInputs);
			}
		}
		return variableInputs;
	}

	/**
	 * Simplify graph one edge at a time
	 * 
	 * @param gate
	 * @param variableInputs
	 */
	public void simplifyVertex(Gate gate, List<Gate> variableInputs) {
		LinkedList<Edge> edges = new LinkedList<Edge>(getOutEdges(gate));
		while (!edges.isEmpty()) {
			Edge e;
			Gate dest = getDest(e = edges.pop());
			Gate otherGate, not;
			Edge newEdge;
			if (dest == null) {
				System.err.println("Edge " + e + " without endpoints");
			}
			switch (dest.getType()) {
			case OUTPUT:
				values.put(dest, values.get(gate));
				break;
			case NOT:
				values.put(dest, !values.get(gate));
				for (Edge edge : getOutEdges(dest)) {
					edges.push(edge);
				}
				break;
			case AND:
				otherGate = getOtherSource(gate, dest);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, values.get(gate) ? otherGate : gate,
							getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!values.get(gate) ? otherGate : gate, variableInputs);
				break;
			case OR:
				otherGate = getOtherSource(gate, dest);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, values.get(gate) ? gate : otherGate,
							getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!values.get(gate) ? gate : otherGate, variableInputs);
				break;
			case XOR:
				// Gate will depend on other gate, circumvent and remove gate
				otherGate = getOtherSource(gate, dest);
				if (values.get(gate)) {
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
				addEdge(new Edge(), values.get(gate) ? otherGate : gate, not,
						EdgeType.DIRECTED);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, not, getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!values.get(gate) ? otherGate : gate, variableInputs);
				break;
			case NOR:
				otherGate = getOtherSource(gate, dest);
				not = GateFactory.getNotGate();
				addVertex(not);
				addEdge(new Edge(), values.get(gate) ? gate : otherGate, not,
						EdgeType.DIRECTED);
				for (Edge edge : getOutEdges(dest)) {
					newEdge = new Edge();
					addEdge(newEdge, not, getDest(edge), EdgeType.DIRECTED);
					edges.push(newEdge);
				}
				removeVertex(dest);
				simplifyUp(!values.get(gate) ? gate : otherGate, variableInputs);
				break;
			case XNOR:
				// Gate will depend on other gate, circumvent and remove gate
				otherGate = getOtherSource(gate, dest);
				if (values.get(gate)) {
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

	/**
	 * Recursively remove gates without successors
	 * 
	 * @param vertex
	 * @param variableInputs
	 */
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

	/* -------------------- METHODS FOR RETRIEVING GATES -------------------- */

	/**
	 * Returns list of input nodes
	 * 
	 * @return input nodes
	 */
	public List<Gate> getInputNodes() {
		return inputNodes;
	}

	/**
	 * Returns new input node
	 * 
	 * @return input node
	 */
	public Gate getInputNode() {
		Gate gate = GateFactory.getInputNode();
		addVertex(gate);
		return gate;
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
	 * Returns new output node
	 * 
	 * @return output node
	 */
	public Gate getOutputNode() {
		Gate gate = GateFactory.getOutputNode();
		addVertex(gate);
		return gate;
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
	 * Returns new not gate
	 * 
	 * @return not gate
	 */
	public Gate getNotGate() {
		Gate gate = GateFactory.getNotGate();
		addVertex(gate);
		return gate;
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
	 * Returns new and gate
	 * 
	 * @return and gate
	 */
	public Gate getAndGate() {
		Gate gate = GateFactory.getAndGate();
		addVertex(gate);
		return gate;
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
	 * Returns new or gate
	 * 
	 * @return or gate
	 */
	public Gate getOrGate() {
		Gate gate = GateFactory.getOrGate();
		addVertex(gate);
		return gate;
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
	 * Returns new xor gate
	 * 
	 * @return xor gate
	 */
	public Gate getXorGate() {
		Gate gate = GateFactory.getXorGate();
		addVertex(gate);
		return gate;
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
	 * Returns new nand gate
	 * 
	 * @return nand gate
	 */
	public Gate getNandGate() {
		Gate gate = GateFactory.getNandGate();
		addVertex(gate);
		return gate;
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
	 * Returns new nor gate
	 * 
	 * @return nor gate
	 */
	public Gate getNorGate() {
		Gate gate = GateFactory.getNorGate();
		addVertex(gate);
		return gate;
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
	 * Returns new xnor gate
	 * 
	 * @return xnor gate
	 */
	public Gate getXnorGate() {
		Gate gate = GateFactory.getXnorGate();
		addVertex(gate);
		return gate;
	}

	/* -------------------- STATIC HELPER METHODS -------------------- */
	
	/**
	 * Returns random boolean
	 * 
	 * @return boolean
	 */
	public static boolean getRandBoolean() {
		return random.nextBoolean();
	}

	/**
	 * Returns random integer with exclusive upper bound n
	 * 
	 * @param n
	 * @return integer
	 */
	public static int getRandInt(int n) {
		return random.nextInt(n);
	}

	/**
	 * Converts integer to list of booleans dependent on bit data
	 * 
	 * @param number
	 * @return list
	 */
	public static List<Boolean> intToBooleanList(int number) {
		List<Boolean> list = new ArrayList<Boolean>();
		String intString = Integer.toBinaryString(number);
		for (int i = 0; i < intString.length(); i++) {
			list.add(intString.charAt(i) == '1');
		}
		return list;
	}

	/**
	 * Returns string of 0s and 1s from list of booleans
	 * 
	 * @param list
	 * @return string
	 */
	public static String booleanListToString(List<Boolean> list) {
		StringBuilder sb = new StringBuilder();
		for (Boolean value : list) {
			if (value != null) {
				sb.append(value ? "1" : "0");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns list of booleans from string of 0s and 1s
	 * 
	 * @param string
	 * @return list
	 */
	public static List<Boolean> stringToBooleanList(String string) {
		List<Boolean> list = new ArrayList<Boolean>(string.length());
		for (int i = 0; i < string.length(); i++) {
			list.add(string.charAt(i) == '1');
		}
		return list;
	}

	/**
	 * Returns hex representation of binary string
	 * 
	 * @param binaryStr
	 * @return hexString
	 */
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
