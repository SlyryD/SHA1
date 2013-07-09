package edu.caar.sha.jung;

/**
 * Static factory that creates gates
 * 
 * @author Ryan
 */
public class GateFactory {

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
	public static int getNextNum(Gate.Type type) {
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
	public static Gate getInputNode() {
		return new Gate(Gate.Type.INPUT, getNextNum(Gate.Type.INPUT));
	}

	/**
	 * Returns output node vertex
	 * 
	 * @return output node
	 */
	public static Gate getOutputNode() {
		return new Gate(Gate.Type.OUTPUT, getNextNum(Gate.Type.OUTPUT));
	}

	/**
	 * Returns not gate vertex
	 * 
	 * @return not gate
	 */
	public static Gate getNotGate() {
		return new Gate(Gate.Type.NOT, getNextNum(Gate.Type.NOT));
	}

	/**
	 * Returns and gate vertex
	 * 
	 * @return and gate
	 */
	public static Gate getAndGate() {
		return new Gate(Gate.Type.AND, getNextNum(Gate.Type.AND));
	}

	/**
	 * Returns or gate vertex
	 * 
	 * @return or gate
	 */
	public static Gate getOrGate() {
		return new Gate(Gate.Type.OR, getNextNum(Gate.Type.OR));
	}

	/**
	 * Returns xor gate vertex
	 * 
	 * @return xor gate
	 */
	public static Gate getXorGate() {
		return new Gate(Gate.Type.XOR, getNextNum(Gate.Type.XOR));
	}

	/**
	 * Returns nand gate vertex
	 * 
	 * @return nand gate
	 */
	public static Gate getNandGate() {
		return new Gate(Gate.Type.NAND, getNextNum(Gate.Type.NAND));
	}

	/**
	 * Returns nor gate vertex
	 * 
	 * @return nor gate
	 */
	public static Gate getNorGate() {
		return new Gate(Gate.Type.NOR, getNextNum(Gate.Type.NOR));
	}

	/**
	 * Returns xnor gate vertex
	 * 
	 * @return xnor gate
	 */
	public static Gate getXnorGate() {
		return new Gate(Gate.Type.XNOR, getNextNum(Gate.Type.XNOR));
	}

	/**
	 * Returns sink vertex
	 * 
	 * @return sink
	 */
	public static Gate getSink() {
		return new Gate(Gate.Type.SINK, getNextNum(Gate.Type.SINK));
	}

}
