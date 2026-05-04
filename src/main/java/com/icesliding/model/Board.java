package com.icesliding.model;

import java.util.Arrays;

public class Board {
    public int rows, cols;
    public char[][] grid;
    public int[][] cost;
    public int startRow, startCol;
    public int goalRow, goalCol;
    public int[][] checkpointPos;
    public int maxCheckpoint;

    public Board(int rows, int cols) {
        this.rows=rows;
        this.cols=cols;
        grid=new char[rows][cols];
        cost=new int[rows][cols];
        checkpointPos=new int[10][2];
        for (int[] pos : checkpointPos){
            Arrays.fill(pos, -1);
        }
        maxCheckpoint=-1;
    }

    public boolean inBounds(int r, int c) {
        return r>=0 && r < rows && c>=0 && c < cols;
    }
}