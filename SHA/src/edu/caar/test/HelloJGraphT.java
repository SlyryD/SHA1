package edu.caar.test;

/** ==========================================
 ** JGraphT : a free Java graph-theory library
 ** ==========================================
 **
 ** Project Info:  [http://jgrapht.sourceforge.net/](http://jgrapht.sourceforge.net/)
 ** Project Creator:  Barak Naveh ([http://sourceforge.net/users/barak_naveh)](http://sourceforge.net/users/barak_naveh))
 **
 ** (C) Copyright 2003-2006, by Barak Naveh and Contributors.
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published by
 ** the Free Software Foundation; either version 2.1 of the License, or
 ** (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 ** or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 ** License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc.,
 ** 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 **/
/** &#8212;&#8212;&#8212;&#8212;&#8212;&#8211;
 ** HelloJGraphT.java
 ** &#8212;&#8212;&#8212;&#8212;&#8212;&#8211;
 ** (C) Copyright 2003-2006, by Barak Naveh and Contributors.
 **
 ** Original Author:  Barak Naveh
 ** Contributor(s):   -
 **
 ** $Id: HelloJGraphT.java 504 2006-07-03 02:37:26Z perfecthash $
 **
 ** Changes
 ** &#8212;&#8212;-
 ** 27-Jul-2003 : Initial revision (BN);
 **
 */

import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/***
 ** A simple introduction to using JGraphT.
 ** 
 ** @author Barak Naveh
 ** @since Jul 27, 2003
 **/
public final class HelloJGraphT {

	// Constructors

	private HelloJGraphT() {
	} // ensure non-instantiability.

	// Methods

	/***
	 ** Creates a toy directed graph based on URL objects that represents link
	 * structure.
	 ** 
	 ** @return a graph based on URL objects.
	 **/
	private static DirectedGraph<URL, DefaultEdge> createHrefGraph() {
		DirectedGraph<URL, DefaultEdge> g = new DefaultDirectedGraph<URL, DefaultEdge>(
				DefaultEdge.class);

		try {
			URL amazon = new URL("http://www.amazon.com");
			URL yahoo = new URL("http://www.yahoo.com");
			URL ebay = new URL("http://www.ebay.com");

			// add the vertices
			g.addVertex(amazon);
			g.addVertex(yahoo);
			g.addVertex(ebay);

			// add edges to create linking structure
			g.addEdge(yahoo, amazon);
			g.addEdge(yahoo, ebay);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return g;
	}

	private static void createAndShowGUI() {
		// create a graph based on URL objects
		DirectedGraph<URL, DefaultEdge> hrefGraph = createHrefGraph();

		// note directed edges are printed as: (<v1>,<v2>)
		System.out.println(hrefGraph.toString());

		// create a visualization using JGraph, via the adapter
		JFrame jframe = new JFrame("Graph");
		JGraphModelAdapter<URL, DefaultEdge> adapter = new JGraphModelAdapter<URL, DefaultEdge>(
				hrefGraph);
		JGraph jgraph = new JGraph(adapter);

		// position vertices
		for (URL url : hrefGraph.vertexSet()) {
			positionVertexAt(adapter, url, 10 + (int) (Math.random() * 390),
					10 + (int) (Math.random() * 390));
		}

		// create display frame
		JScrollPane graphPane = new JScrollPane(jgraph);
		jframe.add(graphPane);

		// display visualization
		jframe.pack();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}

	private static void positionVertexAt(
			JGraphModelAdapter<URL, DefaultEdge> adapter, Object vertex, int x,
			int y) {
		DefaultGraphCell cell = adapter.getVertexCell(vertex);
		AttributeMap attr = cell.getAttributes();
		Rectangle b = GraphConstants.getBounds(attr).getBounds();

		GraphConstants.setBounds(attr, new Rectangle(x, y, b.width, b.height));

		Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<DefaultGraphCell, AttributeMap>();
		cellAttr.put(cell, attr);
		adapter.edit(cellAttr, null, null, null);
	}

	/***
	 ** The starting point for the demo.
	 ** 
	 ** @param args
	 *            ignored.
	 */
	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
