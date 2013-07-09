package edu.caar.sha.jgrapht;

/**
 * Defines gate properties
 * 
 * @author Ryan
 */
public class GateOld {

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
	// Value of circuit at gate and evaluated flag
	private boolean value, evaluated;
	// Number (id) of gate
	private int number;

	/**
	 * Constructs gate with given type and id number
	 * 
	 * @param type
	 * @param number
	 */
	public GateOld(Type type, int number) {
		this.type = type;
		evaluated = false;
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
	 * Returns value of circuit at gate
	 * 
	 * @return value
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * Sets value of circuit as gate
	 * 
	 * @param value
	 */
	public void setValue(boolean value) {
		this.value = value;
		evaluated = true;
	}

	/**
	 * Returns whether value of circuit at gate has been evaluated
	 * 
	 * @return evaluated
	 */
	public boolean isEvaluated() {
		return evaluated;
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
	 * @param gateOld
	 * @return equals
	 */
	public boolean equals(GateOld gateOld) {
		return number == gateOld.number;
	}

	/**
	 * Returns hash code of gate
	 * 
	 * @return number
	 */
	public int hashCode() {
		return number;
	}

	/**
	 * Returns string representation of gate
	 * 
	 * @return string representation of gate
	 */
	public String toString() {
		switch (type) {
		case INPUT:
			return "INPUT" + number + "=" + value;
		case OUTPUT:
			return "OUTPUT" + number + "=" + value;
		case NOT:
			return "NOT" + number + "=" + value;
		case AND:
			return "AND" + number + "=" + value;
		case OR:
			return "OR" + number + "=" + value;
		case XOR:
			return "XOR" + number + "=" + value;
		case NAND:
			return "NAND" + number + "=" + value;
		case NOR:
			return "NOR" + number + "=" + value;
		case XNOR:
			return "XNOR" + number + "=" + value;
		default:
			return "GATE" + number + "=" + value;
		}
	}

}