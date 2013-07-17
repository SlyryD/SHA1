package edu.caar.sha.jung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * Responsible for displaying JGraphT graph using JGraph
 * 
 * @author Ryan
 */
public class DisplayCircuit {

	// Circuit to display
	private BooleanCircuit circuit;

	/**
	 * Constructs DisplayCircuit with given circuit
	 * 
	 * @param circuit
	 */
	public DisplayCircuit(BooleanCircuit circuit) {
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

	private void createAndShowGUI() {
		// Layout<V, E>, VisualizationComponent<V,E>
		Layout<Gate, Edge> layout = new FRLayout2<Gate, Edge>(circuit);
		layout.setSize(new Dimension(889, 500));
		BasicVisualizationServer<Gate, Edge> server = new BasicVisualizationServer<Gate, Edge>(
				layout);
		server.setPreferredSize(new Dimension(889, 500));

		// Setup up a new vertex to shape transformer
		Transformer<Gate, Shape> vertexShape = new Transformer<Gate, Shape>() {

			/**
			 * Returns shape for negation symbol
			 * 
			 * @return negation symbol
			 */
			private Ellipse2D getNegationSymbol() {
				Ellipse2D negationSymbol = new Ellipse2D.Float();
				negationSymbol.setFrame(25, -3, 6, 6);
				return negationSymbol;
			}

			@Override
			public Shape transform(Gate gate) {
				GeneralPath shape = new GeneralPath();
				switch (gate.getType()) {
				case NOT:
					shape.moveTo(25f, 0f);
					shape.lineTo(0f, 10f);
					shape.lineTo(0f, -10f);
					shape.lineTo(25f, 0f);
					shape.append(getNegationSymbol(), false);
					break;
				case NAND:
					shape.append(getNegationSymbol(), false);
				case AND:
					shape.moveTo(15f, -10f);
					shape.lineTo(0f, -10f);
					shape.lineTo(0f, 10f);
					shape.lineTo(15f, 10f);
					shape.append(new Arc2D.Float(5f, -10f, 20f, 20f, 270f,
							180f, Arc2D.OPEN), true);
					break;
				case NOR:
					shape.append(getNegationSymbol(), false);
				case OR:
					shape.moveTo(-5f, 10f);
					shape.append(new Arc2D.Float(-30f, -10f, 50f, 20f, 270f,
							180f, Arc2D.OPEN), true);
					shape.append(new Arc2D.Float(-10f, -10f, 10f, 20f, 90f,
							-180f, Arc2D.OPEN), true);
					break;
				case XNOR:
					shape.append(getNegationSymbol(), false);
				case XOR:
					shape.moveTo(-5f, 10f);
					shape.append(new Arc2D.Float(-30f, -10f, 50f, 20f, 270f,
							180f, Arc2D.OPEN), true);
					shape.append(new Arc2D.Float(-10f, -10f, 10f, 20f, 90f,
							-180f, Arc2D.OPEN), true);
					shape.append(new Arc2D.Float(-13f, -10f, 10f, 20f, 270f,
							180f, Arc2D.OPEN), false);
					break;
				case INPUT:
				case OUTPUT:
				default:
					return new Ellipse2D.Float(-10f, -10f, 20f, 20f);
				}
				return shape;
			}
		};

		// Setup up a new vertex to paint transformer
		Transformer<Gate, Paint> vertexPaint = new Transformer<Gate, Paint>() {
			public Paint transform(Gate gate) {
				switch (gate.getType()) {
				case INPUT:
					return Color.GREEN;
				case OUTPUT:
					return Color.RED;
				case NOT:
					return Color.PINK;
				case AND:
					return Color.YELLOW;
				case OR:
					return Color.BLUE;
				case XOR:
					return Color.CYAN;
				case NAND:
					return Color.MAGENTA;
				case NOR:
					return Color.ORANGE;
				case XNOR:
					return Color.WHITE;
				default:
					return Color.LIGHT_GRAY;
				}
			}
		};

		// Set up a new stroke Transformer for the edges
		float dash[] = { 10.0f };
		final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		Transformer<Edge, Stroke> edgeStrokeTransformer = new Transformer<Edge, Stroke>() {
			public Stroke transform(Edge s) {
				return edgeStroke;
			}
		};

		Transformer<Edge, Paint> edgePaint = new Transformer<Edge, Paint>() {

			@Override
			public Paint transform(Edge edge) {
				if (circuit.getMinCutEdges().contains(edge)) {
					return Color.RED;
				}
				return Color.BLACK;
			}
		};

		Transformer<Gate, String> labelPaint = new Transformer<Gate, String>() {

			@Override
			public String transform(Gate gate) {
				return gate.toString() + "=" + circuit.getValue(gate);
			}
		};

		server.getRenderContext().setVertexShapeTransformer(vertexShape);
		server.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		server.getRenderContext().setEdgeStrokeTransformer(
				edgeStrokeTransformer);
		server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		server.getRenderContext().setVertexLabelTransformer(labelPaint);
		server.getRenderContext().setEdgeLabelTransformer(
				new ToStringLabeller<Edge>());
		server.getRenderer().getVertexLabelRenderer()
				.setPosition(Position.CNTR);

		JFrame frame = new JFrame("Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(server);
		frame.pack();
		frame.setVisible(true);
	}
}
