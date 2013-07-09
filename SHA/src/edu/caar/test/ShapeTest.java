package edu.caar.test;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ShapeTest extends JPanel {

	private static final long serialVersionUID = -1671094495761022813L;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawArc(-20, 0, 50, 20, 270, 180);
		g.drawArc(0, 0, 10, 20, 90, -180);
//		g.drawArc(-5, 0, 10, 20, 270, 180);
	}

	public static void createAndShowGUI() {
		JFrame frame = new JFrame("Shapes");
		JPanel test = new ShapeTest();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(test);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
