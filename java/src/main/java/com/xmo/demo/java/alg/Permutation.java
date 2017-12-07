package com.xmo.demo.java.alg;

public class Permutation {

    public static void main(String[] args) {
        char[] array = { 'a', 'b', 'c' };
        boolean[] bitmap = new boolean[array.length];
        StringBuilder sb = new StringBuilder();
        // permutation(array, bitmap, sb);

        char[] array4c = { 'a', 'b', 'c', 'd', 'e' };
        // char[] array4c = { 'a', 'b', 'c'};
        sb.setLength(0);
        // combination(array4c, 0, 3, sb);

        String[] map = { "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz" };
        sb.setLength(0);
        char[] numbers = {'1', '2', '3'};
        permutationOfPhone(numbers, map, 0, sb);
    }

    public static void permutation(char[] array, boolean[] bitmap, StringBuilder sb) {
        if (sb.length() >= array.length) {
            System.out.println(sb.toString());
            return;
        }
        for (int i = 0; i < array.length; i++) {
            if (!bitmap[i]) {
                sb.append(array[i]);
                bitmap[i] = true;
                permutation(array, bitmap, sb);
                bitmap[i] = false;
                sb.setLength(sb.length() - 1);
            }
        }
    }

    public static void combination(char[] array, int start, int length, StringBuilder sb) {
        if (length == 0) {
            System.out.println(sb.toString());
            return;
        }
        for (int i = start; i < array.length; i++) {
            sb.append(array[i]);
            combination(array, i + 1, length - 1, sb);
            // bitmap[i] = false;
            sb.setLength(sb.length() - 1);
        }
    }
    //leetcode 17
    public static void permutationOfPhone(char[] numbers, String[] map, int start, StringBuilder sb) {
        if(start >= numbers.length) {
            System.out.println(sb.toString());
            return;
        }
        char number = numbers[start];
        int numberIdex = number - '0';
        String letters = map[numberIdex];
        if (letters.isEmpty()) {
            permutationOfPhone(numbers, map, start+1,sb);
        } else {
            for (int i = 0; i < letters.length(); i++) {
                sb.append(letters.charAt(i));
                permutationOfPhone(numbers, map, start+1,sb);
                sb.setLength(sb.length() - 1);
            }
        }
    }

}
