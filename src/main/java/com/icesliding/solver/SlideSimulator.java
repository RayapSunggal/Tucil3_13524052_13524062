package com.icesliding.solver;

import com.icesliding.model.Board;

public class SlideSimulator {

    private static final int[][] DIRS={{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static final char[]  NAMES={'U', 'D', 'L', 'R'};

    public static int[] slide(Board board, int row, int col, int dirIdx, int nextCheckpoint) {
        int dr=DIRS[dirIdx][0], dc=DIRS[dirIdx][1];
        int r=row, c=col;
        int cost=0;
        int cp=nextCheckpoint;

        while (true) {
            int nr=r + dr, nc=c + dc;

            if (!board.inBounds(nr, nc)) return null;
            char next=board.grid[nr][nc];
            if (next=='X') break;
            if (next=='L') return null;

            r=nr;
            c=nc;
            cost+=board.cost[r][c];

            if (next>='0' && next<='9') {
                int ci=next - '0';
                if (ci==cp) {
                    cp++;
                } else if (ci > cp) {
                    return null;
                }
            }
        }

        if (r==row && c==col) return null;
        return new int[]{r, c, cost, cp};
    }

    public static int  dirCount()    { return DIRS.length; }
    public static char dirName(int i) { return NAMES[i]; }
}