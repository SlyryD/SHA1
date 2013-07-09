package edu.caar.sha.jgrapht;

/**
 * Static factory that creates gates
 * 
 * @author Ryan
 */
public class GateFactoryOld {

	// Automatic id numbering for different types of gates
	private static int inNum = 0, outNum = 0, notNum = 0, andNum = 0,
			orNum = 0, xorNum = 0, nandNum = 0, norNum = 0, xnorNum = 0,
			sinkNum = 0;

	/**
	 * Gets next id number for each type of gate
	 * 
	 * @param type
	 * @return number
	 */
	public static int getNextNum(GateOld.Type type) {
		switch (type) {
		case INPUT:
			return inNum++;
		case OUTPUT:
			return outNum++;
		case NOT:
			return notNum++;
		case AND:
			return andNum++;
		case OR:
			return orNum++;
		case XOR:
			return xorNum++;
		case NAND:
			return nandNum++;
		case NOR:
			return norNum++;
		case XNOR:
			return xnorNum++;
		default:
			return sinkNum++;
		}
	}

	/**
	 * Returns input node vertex
	 * 
	 * @return input node
	 */
	public static GateOld getInputNode() {
		return new GateOld(GateOld.Type.INPUT, getNextNum(GateOld.Type.INPUT));
	}

	/**
	 * Returns output node vertex
	 * 
	 * @return output node
	 */
	public static GateOld getOutputNode() {
		return new GateOld(GateOld.Type.OUTPUT, getNextNum(GateOld.Type.OUTPUT));
	}

	/**
	 * Returns not gate vertex
	 * 
	 * @return not gate
	 */
	public static GateOld getNotGate() {
		return new GateOld(GateOld.Type.NOT, getNextNum(GateOld.Type.NOT));
	}

	/**
	 * Returns and gate vertex
	 * 
	 * @return and gate
	 */
	public static GateOld getAndGate() {
		return new GateOld(GateOld.Type.AND, getNextNum(GateOld.Type.AND));
	}

	/**
	 * Returns or gate vertex
	 * 
	 * @return or gate
	 */
	public static GateOld getOrGate() {
		return new GateOld(GateOld.Type.OR, getNextNum(GateOld.Type.OR));
	}

	/**
	 * Returns xor gate vertex
	 * 
	 * @return xor gate
	 */
	public static GateOld getXorGate() {
		return new GateOld(GateOld.Type.XOR, getNextNum(GateOld.Type.XOR));
	}

	/**
	 * Returns nand gate vertex
	 * 
	 * @return nand gate
	 */
	public static GateOld getNandGate() {
		return new GateOld(GateOld.Type.NAND, getNextNum(GateOld.Type.NAND));
	}

	/**
	 * Returns nor gate vertex
	 * 
	 * @return nor gate
	 */
	public static GateOld getNorGate() {
		return new GateOld(GateOld.Type.NOR, getNextNum(GateOld.Type.NOR));
	}

	/**
	 * Returns xnor gate vertex
	 * 
	 * @return xnor gate
	 */
	public static GateOld getXnorGate() {
		return new GateOld(GateOld.Type.XNOR, getNextNum(GateOld.Type.XNOR));
	}

	/**
	 * Returns sink vertex
	 * 
	 * @return sink
	 */
	public static GateOld getSink() {
		return new GateOld(GateOld.Type.SINK, getNextNum(GateOld.Type.SINK));
	}

}
