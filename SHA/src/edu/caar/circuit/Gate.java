package edu.caar.circuit;

/**
 * Defines gate properties
 * 
 * @author Ryan
 */
public class Gate {

	/**
	 * Enumerates types of gates
	 * 
	 * @author Ryan
	 */
	public enum Type {
		INPUT, OUTPUT, NOT, AND, OR, XOR, NAND, NOR, XNOR, SINK
	}

	// Gate type
	private Type type;
	// Number (id) of gate
	private int number;

	/**
	 * Constructs gate with given type and id number
	 * 
	 * @param type
	 * @param number
	 */
	public Gate(Type type, int number) {
		this.type = type;
		this.number = number;
	}

	/**
	 * Returns type of gate
	 * 
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns id number of gate
	 * 
	 * @return number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns whether gate is equal to parameter
	 * 
	 * @param gate
	 * @return equals
	 */
	public boolean equals(Gate gate) {
		return type == gate.getType() && number == gate.number;
	}

	/**
	 * Returns hash code of gate
	 * 
	 * @return number
	 */
	public int hashCode() {
		String string = toString();
		return string.hashCode();
	}

	/**
	 * Returns string representation of gate
	 * 
	 * @return string representation of gate
	 */
	public String toString() {
		switch (type) {
		case INPUT:
			return "INPUT" + number;
		case OUTPUT:
			return "OUTPUT" + number;
		case NOT:
			return "NOT" + number;
		case AND:
			return "AND" + number;
		case OR:
			return "OR" + number;
		case XOR:
			return "XOR" + number;
		case NAND:
			return "NAND" + number;
		case NOR:
			return "NOR" + number;
		case XNOR:
			return "XNOR" + number;
		default:
			return "GATE" + number;
		}
	}

}