package com.icesliding.solver;

import com.icesliding.model.Board;
import com.icesliding.model.GameState;

import java.util.PriorityQueue;

public class UCS {

    public SolverResult solve(Board board) {
        long t0 = System.currentTimeMillis();

        GameState initial = new GameState(board.startRow, board.startCol, 0, 0, null, '\0');

        PriorityQueue<GameState> open = new PriorityQueue<>(
                (a, b) -> Integer.compare(a.gCost, b.gCost));

        java.util.Set<String> visited = new java.util.HashSet<>();
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
}
