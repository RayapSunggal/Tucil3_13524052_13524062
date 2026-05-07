package com.icesliding.solver;

import com.icesliding.model.Board;
import com.icesliding.model.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class GBFS {

    private final int hNum;

    public GBFS(int heuristicNum) { this.hNum = heuristicNum; }

    public SolverResult solve(Board board) {
        long t0 = System.currentTimeMillis();

        GameState initial = new GameState(board.startRow, board.startCol, 0, 0, null, '\0');

        // GBFS: order by heuristic only, ignoring path cost
        PriorityQueue<GameState> open = new PriorityQueue<>(
                (a, b) -> Double.compare(h(a, board), h(b, board)));

        Map<String, Boolean> visited = new HashMap<>();
        open.add(initial);
        int nodesVisited = 0;

        while (!open.isEmpty()) {
            GameState curr = open.poll();
            String key = curr.key();

            if (visited.containsKey(key)) continue;
            visited.put(key, true);
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

                if (!visited.containsKey(next.key())) {
                    open.add(next);
                }
            }
        }

        return SolverResult.notFound(nodesVisited, System.currentTimeMillis() - t0);
    }

    private double h(GameState s, Board board) {
        switch (hNum) {
            case 2:  return h2(s, board);
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

    // H2: Euclidean ke checkpoint berikutnya (jika ada), lalu ke tujuan
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
}
