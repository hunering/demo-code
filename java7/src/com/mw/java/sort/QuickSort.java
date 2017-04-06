package com.mw.java.sort;

public class QuickSort {

	public static void main(String[] args) {
		//int[] numbers = { 1, 2, 3, 4, 5, 6 };
		int[] numbers = { 6, 5, 4, 3, 2, 1 };
		sort(numbers, 0, numbers.length - 1);

		for (int i : numbers) {
			System.out.println(i);
		}

	}

	public static void sort(int[] numbers, int start, int end) {
		if (start >= end) {
			return;
		}

		int i = start + 1, j = end;

		int stone = numbers[start];

		while (true) {

			for (; j > i; j--) {
				if (numbers[j] <= stone) {
					break;
				}
			}

			for (; i < j; i++) {
				if (numbers[i] > stone) {
					break;
				}
			}

			if (i >= j) {
				if (i == j && stone > numbers[i]) {
					numbers[start] = numbers[i];
					numbers[i] = stone;
				}
				break;
			} else {
				int temp = numbers[i];
				numbers[i] = numbers[j];
				numbers[j] = temp;
			}
		}
		sort(numbers, start, i - 1);
		sort(numbers, i, end);
	}

}
