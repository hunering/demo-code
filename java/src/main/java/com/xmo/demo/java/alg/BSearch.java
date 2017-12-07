package com.xmo.demo.java.alg;

public class BSearch {

    public static void main(String[] args) {
        int[] array = { 1, 2, 3, 4, 5, 6, 6, 7 };
        System.out.println(bSearch(array, 10, 0, array.length - 1));
    }

    public static int bSearch(int[] array, int value, int start, int end) {
        if (start > end) {
            return -1;
        }

        if (start == end) {
            if (array[start] != value) {
                return -1;
            } else {
                return start;
            }
        }

        int iMiddle = (start + end) / 2;

        if (array[iMiddle] > value) {
            return bSearch(array, value, start, iMiddle - 1);
        } else if (array[iMiddle] < value) {
            return bSearch(array, value, iMiddle + 1, end);
        } else {
            return iMiddle;
        }
    }

}
