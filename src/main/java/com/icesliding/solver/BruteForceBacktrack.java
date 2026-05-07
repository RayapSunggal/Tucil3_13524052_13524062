package com.icesliding.solver;

import com.icesliding.model.Board;
import com.icesliding.model.GameState;

import java.util.HashSet;
import java.util.Set;

public class BruteForceBacktrack {

    private int       nodesVisited;
    private GameState bestGoal;
    private long      t0;

    public SolverResult solve(Board board) {
        t0           = System.currentTimeMillis();
        nodesVisited = 0;
        bestGoal     = null;

        GameState initial = new GameState(board.startRow, board.startCol, 0, 0, null, '\0');

        Set<String> pathVisited = new HashSet<>();
        pathVisited.add(initial.key());

        dfs(board, initial, pathVisited);

        long elapsed = System.currentTimeMillis() - t0;
        if (bestGoal != null) {
            return SolverResult.found(bestGoal, nodesVisited, elapsed);
        }
        return SolverResult.notFound(nodesVisited, elapsed);
    }

    private void dfs(Board board, GameState curr, Set<String> pathVisited) {
        nodesVisited++;

        if (curr.nextCheckpoint > board.maxCheckpoint
                && curr.row == board.goalRow
                && curr.col == board.goalCol) {
            if (bestGoal == null || curr.gCost < bestGoal.gCost) bestGoal = curr;
            return;
        }

        for (int d = 0; d < SlideSimulator.dirCount(); d++) {
            int[] slide = SlideSimulator.slide(board, curr.row, curr.col, d, curr.nextCheckpoint);
            if (slide == null) continue;

            GameState next = new GameState(
                    slide[0], slide[1], slide[3],
                    curr.gCost + slide[2],
                    curr, SlideSimulator.dirName(d));

            String nextKey = next.key();

            if (pathVisited.contains(nextKey)) continue;
            if (bestGoal != null && next.gCost >= bestGoal.gCost) continue;

            pathVisited.add(nextKey);
            dfs(board, next, pathVisited);
            pathVisited.remove(nextKey);
        }
    }
}
