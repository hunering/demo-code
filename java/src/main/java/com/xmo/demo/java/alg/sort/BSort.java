package com.xmo.demo.java.alg.sort;

public class BSort {

    public static void main(String[] args) {
        int[] array = { 6, 5, 4, 3, 2, 1, };
        bSort(array);

        for (int i : array) {
            System.out.println(i);
        }
    }

    public static void bSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            popOne(array, i);
        }
    }

    public static void popOne(int[] array, int round) {
        for (int i = 0; i < array.length - round-1; i++) {
            if (array[i] > array[i + 1]) {
                int temp = array[i];
                array[i] = array[i + 1];
                array[i + 1] = temp;
            }
        }
    }
}
