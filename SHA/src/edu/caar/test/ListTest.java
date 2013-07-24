package edu.caar.test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {

	public static List<Integer> rotl(List<Integer> list, int number) {
		List<Integer> newList = list.subList(number, 32);
		newList.addAll(list.subList(0, number));
		return newList;
	}

	public static void main(String[] args) {
		List<Boolean> list = new ArrayList<Boolean>(11);

		for (int i = 0; i < 11; i++) {
			list.add(null);
		}

		list.set(0, true);
		list.set(10, false);

		for (Boolean value : list) {
			System.out.println(value);
		}

		System.out.println(Integer.toBinaryString(0xc3d2e1f0));

		List<List<Integer>> subListList = new ArrayList<List<Integer>>();
		for (int i = 0; i < 32; i++) {
			List<Integer> newList = new ArrayList<Integer>();
			for (int j = 0; j < 32; j++) {
				newList.add(j);
			}
			subListList.add(rotl(newList, i));
		}

		System.out.println(subListList);
	}
}
