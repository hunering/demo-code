package com.xmo.demo.java.alg.sort;

public class QSort {

    public static void main(String[] args) {
        int[] array = { 6, 5, 4, 3, 2, 1, 6 };
        qs(array, 0, array.length - 1);

        for (int i : array) {
            System.out.println(i);
        }

    }

    public static void qs(int[] array, int start, int end) {
        if (start < end) {
            int midP = qs_s(array, start, end);
            qs(array, 0, midP - 1);
            qs(array, midP + 1, end);
        }
    }

    public static int qs_s(int[] array, int start, int end) {
        if (start >= end) {
            return start;
        }

        int middle = array[start];
        while (start < end) {
            while (start < end) {
                if (middle < array[end]) {
                    end--;
                } else {
                    array[start] = array[end];
                    array[end] = middle;
                    break;
                }
            }

            while (start < end) {
                if (middle >= array[start]) {
                    start++;
                } else {
                    array[end] = array[start];
                    array[start] = middle;
                    break;
                }
            }
        }
        return start;
    }

}
