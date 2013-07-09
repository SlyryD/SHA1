package edu.caar.sha.jgrapht;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Responsible for displaying JGraphT graph using JGraph
 * 
 * @author Ryan
 */
public class DisplayCircuitOld {

	// Circuit to display
	private BooleanCircuitOld circuit;

	/**
	 * Constructs DisplayCircuit with given circuit
	 * 
	 * @param circuit
	 */
	public DisplayCircuitOld(BooleanCircuitOld circuit) {
		this.circuit = circuit;
	}

	/**
	 * Display circuit on Event Dispatch Thread
	 */
	public void display() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * Display graph in JFrame
	 */
	private void createAndShowGUI() {
		// Create a visualization using JGraph via the adapter
		JFrame jframe = new JFrame("Graph");
		JGraphModelAdapter<GateOld, DefaultWeightedEdge> adapter = new JGraphModelAdapter<GateOld, DefaultWeightedEdge>(
				circuit);
		JGraph jgraph = new JGraph(adapter);

		// Position input nodes
		for (GateOld gateOld : circuit.getInputNodes()) {
			positionVertexAt(adapter, gateOld, 10, 150 * gateOld.getNumber());
		}

		// Position gates
		Set<GateOld> difference = new HashSet<GateOld>(circuit.vertexSet());
		difference.removeAll(circuit.getInputNodes());
		difference.removeAll(circuit.getOutputNodes());
		for (GateOld gateOld : difference) {
			positionVertex(adapter, gateOld);
		}

		// Position output nodes
		for (GateOld gateOld : circuit.getOutputNodes()) {
			positionVertex(adapter, gateOld);
		}

		// Add graph to frame
		JScrollPane graphPane = new JScrollPane(jgraph);
		jframe.add(graphPane);

		// Display visualization
		jframe.pack();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}

	/**
	 * Position vertex based on predecessors in graph
	 * 
	 * @param adapter
	 * @param gateOld
	 */
	private void positionVertex(
			JGraphModelAdapter<GateOld, DefaultWeightedEdge> adapter, GateOld gateOld) {
		Set<GateOld> parents = circuit.getParents(gateOld);
		int x = 0, y = 0;
		for (GateOld parent : parents) {
			Rectangle bounds = GraphConstants.getBounds(
					adapter.getVertexCell(parent).getAttributes()).getBounds();
			if (bounds.x > x) {
				x = bounds.x;
			}
			if (bounds.y > y) {
				y = bounds.y;
			}
		}
		positionVertexAt(adapter, gateOld, x + 150,
				Math.abs(y + ((int) (Math.random() * 175) - 75)));
	}

	/**
	 * Positions vertex at given coordinates in JFrame
	 * 
	 * @param adapter
	 * @param gateOld
	 * @param x
	 * @param y
	 */
	private void positionVertexAt(
			JGraphModelAdapter<GateOld, DefaultWeightedEdge> adapter, GateOld gateOld,
			int x, int y) {
		DefaultGraphCell cell = adapter.getVertexCell(gateOld);
		AttributeMap attr = cell.getAttributes();
		Rectangle bounds = GraphConstants.getBounds(attr).getBounds();

		GraphConstants.setBounds(attr, new Rectangle(x, y, bounds.width,
				bounds.height));

		Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<DefaultGraphCell, AttributeMap>();
		cellAttr.put(cell, attr);
		adapter.edit(cellAttr, null, null, null);
	}

	/**
	 * Creates and displays circuit
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create circuit
		BooleanCircuitOld circuit = new BooleanCircuitOld(DefaultWeightedEdge.class);
		circuit.initializeGraph();
		circuit.evaluateCircuit();

		// Associate DisplayCircuit
		DisplayCircuitOld circuitDisplay = new DisplayCircuitOld(circuit);
		circuitDisplay.display();

		// Print edges
		System.out.println(circuit.toString());
	}

}
