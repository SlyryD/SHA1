package edu.caar.test;

public class TestEndianess {

	/**
	 * Returns gates in reverse byte-endian order
	 * 
	 * @param values
	 * @return gates
	 */
	private static String changeEndianess(String values) {
		StringBuilder newString = new StringBuilder(32);
		for (int i = 4; i > 0; i--) {
			newString.append(values.substring(8 * (i - 1), 8 * i));
		}
		return newString.toString();
	}

	/**
	 * Returns list of gates rotated left given numbers. Does not affect
	 * original list.
	 * 
	 * @param values
	 * @param number
	 * @return rotl(list, number)
	 */
	public static String rotl(String values, int number) {
		String endianList = changeEndianess(values);
		StringBuilder newList = new StringBuilder(endianList.substring(number,
				32));
		newList.append(endianList.substring(0, number));
		return changeEndianess(newList.toString());
	}

	public static String pad(String string) {
		StringBuilder sb = new StringBuilder(string);
		int length = string.length();
		for (; length % 8 != 0; length++) {
			sb.insert(0, '0');
		}
		for (int i = 0; i < 64 - length; i++) {
			sb.append('0');
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Test little endian length rep");
		int a = 48;
		System.out.println(Integer.toBinaryString(a));
		System.out
				.println(pad(Integer.toBinaryString(Integer.reverseBytes(a))));

		System.out.println("Test rotate and change endianess");
		System.out.println("10011101010110010001110100000000");
		System.out.println(changeEndianess("10011101010110010001110100000000"));
		System.out.println("00000011101010110011001110100000");
		System.out.println(rotl("10011101010110010001110100000000", 5));
	}

}
