package edu.caar.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestSpeed {

	private static Random rand = new Random();

	public static String getRandomString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 512; i++) {
			sb.append(rand.nextBoolean() ? "1" : "0");
		}
		return sb.toString();
	}

	public static List<Boolean> getRandomList() {
		List<Boolean> list = new ArrayList<Boolean>();
		for (int i = 0; i < 512; i++) {
			list.add(rand.nextBoolean());
		}
		return list;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime, endTime;

		startTime = System.nanoTime();
		for (int i = 0; i < (int) Math.pow(2, 20); i++) {
			getRandomString();
		}
		endTime = System.nanoTime();
		System.out.println(endTime - startTime + "ns");
		
		startTime = System.nanoTime();
		for (int i = 0; i < (int) Math.pow(2, 20); i++) {
			getRandomList();
		}
		endTime = System.nanoTime();
		System.out.println(endTime - startTime + "ns");
	}

}
