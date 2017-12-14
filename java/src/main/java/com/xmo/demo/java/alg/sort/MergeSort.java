package com.xmo.demo.java.alg.sort;

public class MergeSort {

    public static void main(String[] args) {
        MergeSort s = new MergeSort();
        //int[] array = { 1, 2, 3 };
        //int[] array = { 3, 2, 1 };
        int[] array = { 3, 4, 2, 1 };
        s.arrayBuffer = new int[array.length];
        s.sort(array, 0, array.length - 1);
        for (int i : array) {
            System.out.println(i);
        }
    }

    int[] arrayBuffer;

    public void sort(int[] array, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int middle = (lo + hi) / 2;
        sort(array, lo, middle);
        sort(array, middle + 1, hi);
        merge(array, lo, middle, hi);
    }

    public void merge(int[] array, int lo, int middle, int hi) {

        int i = lo, j = middle + 1, k = lo;
        while (i <= middle && j <= hi) {
            if (array[i] <= array[j]) {
                arrayBuffer[k] = array[i];
                i++;
            } else {
                arrayBuffer[k] = array[j];
                j++;
            }
            k++;
        }

        for (; i <= middle; i++, k++) {
            arrayBuffer[k] = array[i];
        }

        for (; j <= hi; j++, k++) {
            arrayBuffer[k] = array[j];
        }

        for (int t = lo; t <= hi; t++) {
            array[t] = arrayBuffer[t];
        }
    }

}
