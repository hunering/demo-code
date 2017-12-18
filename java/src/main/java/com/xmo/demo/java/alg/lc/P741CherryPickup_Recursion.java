package com.xmo.demo.java.alg.lc;

import java.util.Arrays;

public class P741CherryPickup_Recursion {

    public static void main(String[] args) {
        P741CherryPickup_Recursion p = new P741CherryPickup_Recursion();
        int[][] grid = {
                {1,1,1,0,0},
                {0,0,1,0,1},
                {1,0,1,0,0},
                {0,0,1,0,0},
                {0,0,1,1,1}};
        System.out.println(p.cherryPickup(grid));
    }

    public int cherryPickup(int[][] grid) {
        int N = grid.length;
        int f[][][] = new int[N][N][N];
        for (int[][] layer : f) {
            for (int[] row : layer) {
                Arrays.fill(row, Integer.MIN_VALUE);
            }
        }
        int value = dp(grid, f, 0, 0, 0);
        if (value < 0) {
            value = 0;
        }
        return value;

    }

    public int dp(int[][] grid, int[][][] f, int r1, int c1, int r2) {
        int N = grid.length;
        int c2 = r1 + c1 - r2;
        if (r1 >= N || c1 >= N || r2 >= N || c2 >= N || grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return Integer.MIN_VALUE;
        } else if (r1 == N - 1 && c1 == N - 1 && r2 == N - 1) {
            return grid[N - 1][N - 1];
        } else if (f[r1][c1][r2] >= 0) {
            return f[r1][c1][r2];
        } else {
            int currentValue = grid[r1][c1];
            if (r1 != r2) {
                currentValue += grid[r2][c2];
            }

            currentValue += Math.max(Math.max(dp(grid, f, r1, c1 + 1, r2), dp(grid, f, r1, c1 + 1, r2 + 1)),
                    Math.max(dp(grid, f, r1 + 1, c1, r2 + 1), dp(grid, f, r1 + 1, c1, r2)));
            f[r1][c1][r2] = currentValue;
            return currentValue;
        }
    }
}
