package com.xmo.demo.java.alg.lc;

public class P493ReversePairs {

    public static void main(String[] args) {
        //int[] nums = {1,3,2,3,1};
        //int[] nums = {2,4,3,5,1};
        //int[] nums = {-5,-5};
        int[] nums = {2147483647,2147483647,2147483647,2147483647,2147483647,2147483647};
        P493ReversePairs p = new P493ReversePairs();
        System.out.println(p.reversePairs(nums));
    }

    public int reversePairs(int[] nums) {
        arrayBuffer = new int[nums.length];
        sort(nums, 0, nums.length-1);
        return count;
    }

    int[] arrayBuffer;
    int count = 0;

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
            if (array[i] < array[j]) {
                arrayBuffer[k] = array[i];
                i++;
            } else {
                arrayBuffer[k] = array[j];                
                for (int compareIndex = i; compareIndex <= middle; compareIndex++) {
                    //float half = ((float)array[compareIndex]/2
                    if (array[j] < ((float)array[compareIndex])/2) {
                        count++;
                    }
                }
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
