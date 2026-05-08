package com.icesliding.solver;

import com.icesliding.model.Board;
import com.icesliding.model.GameState;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class GBFS {

    private final int hNum;

    public GBFS(int heuristicNum) { this.hNum = heuristicNum; }

    public SolverResult solve(Board board) {
        long t0 = System.currentTimeMillis();

        GameState initial = new GameState(board.startRow, board.startCol, 0, 0, null, '\0');

        // GBFS: order by heuristic only, ignoring path cost
        PriorityQueue<GameState> open = new PriorityQueue<>(
                (a, b) -> Double.compare(h(a, board), h(b, board)));

        Set<String> visited = new HashSet<>();
        open.add(initial);
        int nodesVisited = 0;

        while (!open.isEmpty()) {
            GameState curr = open.poll();
            String key = curr.key();

            if (visited.contains(key)) continue;
            visited.add(key);
            nodesVisited++;

            if (curr.nextCheckpoint > board.maxCheckpoint
                    && curr.row == board.goalRow
                    && curr.col == board.goalCol) {
                return SolverResult.found(curr, nodesVisited, System.currentTimeMillis() - t0);
            }

            for (int d = 0; d < SlideSimulator.dirCount(); d++) {
                int[] slide = SlideSimulator.slide(board, curr.row, curr.col, d, curr.nextCheckpoint);
                if (slide == null) continue;

                int newG = curr.gCost + slide[2];
                GameState next = new GameState(slide[0], slide[1], slide[3], newG, curr, SlideSimulator.dirName(d));

                if (!visited.contains(next.key())) {
                    open.add(next);
                }
            }
        }

        return SolverResult.notFound(nodesVisited, System.currentTimeMillis() - t0);
    }

    private double h(GameState s, Board board) {
        switch (hNum) {
            case 2:  return h2(s, board);
            case 3:  return h3(s, board);
            case 4:  return h4(s, board);
            default: return h1(s, board);
        }
    }

    // H1: Manhattan via checkpoint berikutnya → tujuan
    private int h1(GameState s, Board board) {
        if (s.nextCheckpoint > board.maxCheckpoint)
            return Math.abs(s.row - board.goalRow) + Math.abs(s.col - board.goalCol);
        int cr = board.checkpointPos[s.nextCheckpoint][0];
        int cc = board.checkpointPos[s.nextCheckpoint][1];
        return Math.abs(s.row - cr) + Math.abs(s.col - cc)
                + Math.abs(cr - board.goalRow) + Math.abs(cc - board.goalCol);
    }

    // H2: Euclidean via checkpoint berikutnya → tujuan
    private double h2(GameState s, Board board) {
        if (s.nextCheckpoint > board.maxCheckpoint) {
            double dr = s.row - board.goalRow, dc = s.col - board.goalCol;
            return Math.sqrt(dr * dr + dc * dc);
        }
        int cr = board.checkpointPos[s.nextCheckpoint][0];
        int cc = board.checkpointPos[s.nextCheckpoint][1];
        double dr1 = s.row - cr,           dc1 = s.col - cc;
        double dr2 = cr - board.goalRow,   dc2 = cc - board.goalCol;
        return Math.sqrt(dr1 * dr1 + dc1 * dc1) + Math.sqrt(dr2 * dr2 + dc2 * dc2);
    }

    // H3: Chebyshev distance via checkpoint berikutnya → tujuan
    private double h3(GameState s, Board board) {
        if (s.nextCheckpoint > board.maxCheckpoint)
            return Math.max(Math.abs(s.row - board.goalRow), Math.abs(s.col - board.goalCol));
        int cr = board.checkpointPos[s.nextCheckpoint][0];
        int cc = board.checkpointPos[s.nextCheckpoint][1];
        return Math.max(Math.abs(s.row - cr), Math.abs(s.col - cc))
             + Math.max(Math.abs(cr - board.goalRow), Math.abs(cc - board.goalCol));
    }

    // H4: Minkowski distance (p=3) via checkpoint berikutnya → tujuan
    private double h4(GameState s, Board board) {
        final double P = 3.0;
        if (s.nextCheckpoint > board.maxCheckpoint) {
            double dr = Math.abs(s.row - board.goalRow), dc = Math.abs(s.col - board.goalCol);
            return Math.pow(Math.pow(dr, P) + Math.pow(dc, P), 1.0 / P);
        }
        int cr = board.checkpointPos[s.nextCheckpoint][0];
        int cc = board.checkpointPos[s.nextCheckpoint][1];
        double dr1 = Math.abs(s.row - cr),         dc1 = Math.abs(s.col - cc);
        double dr2 = Math.abs(cr - board.goalRow),  dc2 = Math.abs(cc - board.goalCol);
        return Math.pow(Math.pow(dr1, P) + Math.pow(dc1, P), 1.0 / P)
             + Math.pow(Math.pow(dr2, P) + Math.pow(dc2, P), 1.0 / P);
    }
}
