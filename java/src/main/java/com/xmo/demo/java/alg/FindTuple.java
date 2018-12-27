package com.xmo.demo.java.alg;

public class FindTuple {
	public static void main(String[] args) {
		// give you a sorted array
		// find all the tuples that the sum of the two element equals 15
		int numbers[] = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		int sum = 15;

		int start = 0;
		int end = 9;

		while (start < end) {
			if (numbers[start] + numbers[end] < sum) {
				start++;
			} else if (numbers[start] + numbers[end] > sum) {
				end--;
			} else {
				System.out.println("(" + numbers[start] + "," + numbers[end] + ")");
				start++;
				end--;
			}
		}
	}
}
