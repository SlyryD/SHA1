package edu.caar.sha.jgrapht;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.StoerWagnerMinimumCut;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Models a boolean circuit as a directed acyclic graph. Nodes correspond to
 * gates and edges correspond to wires
 * 
 * @author Ryan
 */
public class BooleanCircuitOld extends
		DefaultDirectedWeightedGraph<GateOld, DefaultWeightedEdge> {

	// Default serialization id
	private static final long serialVersionUID = 1L;
	// Lists of gates in circuit
	private List<GateOld> inputNodes, outputNodes, notGates, andGates, orGates,
			xorGates, nandGates, norGates, xnorGates;

	/**
	 * Constructs boolean circuit with given edge class and initializes
	 * array-based lists of gates
	 * 
	 * @param edgeClass
	 */
	public BooleanCircuitOld(
			java.lang.Class<? extends DefaultWeightedEdge> edgeClass) {
		super(edgeClass);
		inputNodes = new ArrayList<GateOld>();
		outputNodes = new ArrayList<GateOld>();
		notGates = new ArrayList<GateOld>();
		andGates = new ArrayList<GateOld>();
		orGates = new ArrayList<GateOld>();
		xorGates = new ArrayList<GateOld>();
		nandGates = new ArrayList<GateOld>();
		norGates = new ArrayList<GateOld>();
		xnorGates = new ArrayList<GateOld>();
	}

	/**
	 * Sample circuit
	 */
	public void initializeGraph() {
		// Add input vertices
		for (int i = 0; i < 3; i++) {
			addVertex(GateFactoryOld.getInputNode());
		}

		// Add output vertices
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactoryOld.getOutputNode());
		}

		// Add not gates
		for (int i = 0; i < 3; i++) {
			addVertex(GateFactoryOld.getNotGate());
		}

		// Add and gates
		for (int i = 0; i < 2; i++) {
			addVertex(GateFactoryOld.getAndGate());
		}

		// Add or gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactoryOld.getOrGate());
		}

		// Add xor gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactoryOld.getXorGate());
		}

		// Add nand gates
		for (int i = 0; i < 1; i++) {
			addVertex(GateFactoryOld.getNandGate());
		}

		// Add nor gates
		for (int i = 0; i < 0; i++) {
			addVertex(GateFactoryOld.getNorGate());
		}

		// Add xnor gates
		for (int i = 0; i < 0; i++) {
			addVertex(GateFactoryOld.getXnorGate());
		}

		// Add edges to create linking structure
		addEdge(inputNodes.get(0), notGates.get(0));
		addEdge(notGates.get(0), andGates.get(0));
		addEdge(andGates.get(0), notGates.get(1));
		addEdge(notGates.get(1), orGates.get(0));
		addEdge(orGates.get(0), outputNodes.get(0));
		addEdge(inputNodes.get(1), andGates.get(1));
		addEdge(andGates.get(1), andGates.get(0));
		addEdge(andGates.get(1), xorGates.get(0));
		addEdge(xorGates.get(0), orGates.get(0));
		addEdge(inputNodes.get(1), nandGates.get(0));
		addEdge(nandGates.get(0), andGates.get(1));
		addEdge(inputNodes.get(2), nandGates.get(0));
		addEdge(nandGates.get(0), notGates.get(2));
		addEdge(notGates.get(2), xorGates.get(0));
	}

	@Override
	public boolean addVertex(GateOld gateOld) {
		switch (gateOld.getType()) {
		case INPUT:
			inputNodes.add(gateOld);
			break;
		case OUTPUT:
			outputNodes.add(gateOld);
			break;
		case NOT:
			notGates.add(gateOld);
			break;
		case AND:
			andGates.add(gateOld);
			break;
		case OR:
			orGates.add(gateOld);
			break;
		case XOR:
			xorGates.add(gateOld);
			break;
		case NAND:
			nandGates.add(gateOld);
			break;
		case NOR:
			norGates.add(gateOld);
			break;
		case XNOR:
			xnorGates.add(gateOld);
			break;
		default:
			break;
		}
		return super.addVertex(gateOld);
	}

	@Override
	public DefaultWeightedEdge addEdge(GateOld sourceVertex, GateOld targetVertex) {
		DefaultWeightedEdge edge = super.addEdge(sourceVertex, targetVertex);
		setEdgeWeight(edge, 1);
		return edge;
	}

	@Override
	public boolean removeVertex(GateOld gateOld) {
		switch (gateOld.getType()) {
		case INPUT:
			inputNodes.remove(gateOld);
			break;
		case OUTPUT:
			outputNodes.remove(gateOld);
			break;
		case NOT:
			notGates.remove(gateOld);
			break;
		case AND:
			andGates.remove(gateOld);
			break;
		case OR:
			orGates.remove(gateOld);
			break;
		case XOR:
			xorGates.remove(gateOld);
			break;
		case NAND:
			nandGates.remove(gateOld);
			break;
		case NOR:
			norGates.remove(gateOld);
			break;
		case XNOR:
			xnorGates.remove(gateOld);
			break;
		default:
			break;
		}
		return super.removeVertex(gateOld);
	}

	/**
	 * Returns set of predecessors of gate in circuit
	 * 
	 * @param gateOld
	 * @return parents
	 */
	public Set<GateOld> getParents(GateOld gateOld) {
		Set<GateOld> parents = new HashSet<GateOld>();
		for (DefaultWeightedEdge edge : incomingEdgesOf(gateOld)) {
			parents.add(getEdgeSource(edge));
		}
		return parents;
	}

	/**
	 * Returns set of successors of gate in circuit
	 * 
	 * @param gateOld
	 * @return parents
	 */
	public Set<GateOld> getChildren(GateOld gateOld) {
		Set<GateOld> children = new HashSet<GateOld>();
		for (DefaultWeightedEdge edge : outgoingEdgesOf(gateOld)) {
			children.add(getEdgeTarget(edge));
		}
		return children;
	}

	/**
	 * Evaluates circuit at each gate
	 */
	public void evaluateCircuit() {
		for (GateOld gateOld : outputNodes) {
			System.out.println("Current: " + gateOld);
			gateOld.setValue(evaluateVertex(gateOld));
		}
	}

	/**
	 * Recursively evaluates value at each gate in graph
	 * 
	 * @param gateOld
	 * @return value
	 */
	public boolean evaluateVertex(GateOld gateOld) {
		System.out.println("Current: " + gateOld);
		boolean value;
		Iterator<GateOld> parents = getParents(gateOld).iterator();
		switch (gateOld.getType()) {
		case INPUT:
			value = gateOld.getValue();
			break;
		case OUTPUT:
			value = evaluateVertex(parents.next());
			break;
		case NOT:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: !evaluateVertex(parents.next());
			break;
		case AND:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: evaluateVertex(parents.next())
							& evaluateVertex(parents.next());
			break;
		case OR:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: evaluateVertex(parents.next())
							| evaluateVertex(parents.next());
			break;
		case XOR:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: evaluateVertex(parents.next())
							^ evaluateVertex(parents.next());
			break;
		case NAND:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: !(evaluateVertex(parents.next()) & evaluateVertex(parents
							.next()));
			break;
		case NOR:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: !(evaluateVertex(parents.next()) | evaluateVertex(parents
							.next()));
			break;
		case XNOR:
			value = gateOld.isEvaluated() ? gateOld.getValue()
					: !(evaluateVertex(parents.next()) ^ evaluateVertex(parents
							.next()));
			break;
		default:
			value = false;
			break;
		}
		gateOld.setValue(value);
		return value;
	}

	/**
	 * Simplify circuit by removing unnecessary gates
	 */
	public void simplifyCircuit() {
		for (GateOld gateOld : vertexSet()) {
			if (gateOld.isEvaluated()) {
				// TODO: Simplify evaluated vertices
			}
		}
	}

	public Set<GateOld> minCut() {
		// Add sinks to circuit for sake of min cut
		GateOld sink1 = GateFactoryOld.getSink(), sink2 = GateFactoryOld.getSink();
		addVertex(sink1);
		addVertex(sink2);
		for (GateOld gateOld : inputNodes) {
			addEdge(sink1, gateOld);
		}
		for (GateOld gateOld : outputNodes) {
			addEdge(gateOld, sink2);
		}

		// Calculate min cut
		StoerWagnerMinimumCut<GateOld, DefaultWeightedEdge> minimumCut = new StoerWagnerMinimumCut<GateOld, DefaultWeightedEdge>(
				this);
		Set<GateOld> minCut = minimumCut.minCut();
		System.out.println(minimumCut.minCutWeight());

		// Remove sinks
		removeVertex(sink1);
		removeVertex(sink2);

		// Return set of vertices
		return minCut;
	}

	/**
	 * Returns list of input nodes
	 * 
	 * @return input nodes
	 */
	public List<GateOld> getInputNodes() {
		return inputNodes;
	}

	/**
	 * Returns list of output nodes
	 * 
	 * @return output nodes
	 */
	public List<GateOld> getOutputNodes() {
		return outputNodes;
	}

	/**
	 * Returns list of not gates
	 * 
	 * @return not gates
	 */
	public List<GateOld> getNotGates() {
		return notGates;
	}

	/**
	 * Returns list of and gates
	 * 
	 * @return and gates
	 */
	public List<GateOld> getAndGates() {
		return andGates;
	}

	/**
	 * Returns list of or gates
	 * 
	 * @return or gates
	 */
	public List<GateOld> getOrGates() {
		return orGates;
	}

	/**
	 * Returns list of xor gates
	 * 
	 * @return xor gates
	 */
	public List<GateOld> getXorGates() {
		return xorGates;
	}

	/**
	 * Returns list of nand gates
	 * 
	 * @return nand gates
	 */
	public List<GateOld> getNandGates() {
		return nandGates;
	}

	/**
	 * Returns list of nor gates
	 * 
	 * @return nor gates
	 */
	public List<GateOld> getNorGates() {
		return norGates;
	}

	/**
	 * Returns list of xnor gates
	 * 
	 * @return xnor gates
	 */
	public List<GateOld> getXnorGates() {
		return xnorGates;
	}

}
