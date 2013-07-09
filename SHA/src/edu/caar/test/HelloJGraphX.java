package edu.caar.test;

import javax.swing.JFrame;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class HelloJGraphX extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public HelloJGraphX() {
		super("Hello, World!");

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			for (char i = 'A'; i <= 'O'; i++) {
				Object[] vertices = graph.getChildVertices(parent);
				Object v1 = graph.insertVertex(parent, null,
						Character.toString(i), Math.random() * vertices.length
								* 20, Math.random() * vertices.length * 20, 80,
						30);
				for (Object v2 : vertices) {
					graph.insertEdge(parent, null, "Edge", v1, v2);
				}

			}
		} finally {
			graph.getModel().endUpdate();
		}

		mxOrganicLayout layout = new mxOrganicLayout(graph);

		graph.getModel().beginUpdate();
		try {
			layout.execute(parent);
		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
	}

	public static void main(String[] args) {
		HelloJGraphX frame = new HelloJGraphX();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
