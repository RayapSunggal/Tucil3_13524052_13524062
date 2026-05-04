package com.icesliding.solver;

import com.icesliding.model.Board;
import com.icesliding.model.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Astar {

    private final int hNum;

    public Astar(int heuristicNum) { this.hNum=heuristicNum; }

    public SolverResult solve(Board board) {
        long t0=System.currentTimeMillis();

        GameState initial=new GameState(board.startRow, board.startCol, 0, 0, null, '\0');

        PriorityQueue<GameState> open=new PriorityQueue<>(
                (a, b) -> Double.compare(a.gCost + h(a, board), b.gCost + h(b, board)));

        Map<String, Integer> best=new HashMap<>();
        open.add(initial);
        int nodesVisited=0;

        while (!open.isEmpty()) {
            GameState curr=open.poll();
            String key=curr.key();

            if (best.containsKey(key) && best.get(key)<=curr.gCost) continue;
            best.put(key, curr.gCost);
            nodesVisited++;

            if (curr.nextCheckpoint > board.maxCheckpoint
                    && curr.row==board.goalRow
                    && curr.col==board.goalCol) {
                return SolverResult.found(curr, nodesVisited, System.currentTimeMillis() - t0);
            }

            for (int d=0; d < SlideSimulator.dirCount(); d++) {
                int[] slide=SlideSimulator.slide(board, curr.row, curr.col, d, curr.nextCheckpoint);
                if (slide==null) continue;

                int newG=curr.gCost + slide[2];
                GameState next=new GameState(slide[0], slide[1], slide[3], newG, curr, SlideSimulator.dirName(d));
                String nextKey=next.key();

                if (!best.containsKey(nextKey) || best.get(nextKey) > newG) {
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
            default: return h1(s, board);
        }
    }

    private int h1(GameState s, Board board) {
        return Math.abs(s.row - board.goalRow) + Math.abs(s.col - board.goalCol);
    }

    private int h2(GameState s, Board board) {
        if (s.nextCheckpoint > board.maxCheckpoint) return h1(s, board);
        int cr=board.checkpointPos[s.nextCheckpoint][0];
        int cc=board.checkpointPos[s.nextCheckpoint][1];
        return Math.abs(s.row - cr) + Math.abs(s.col - cc)
                + Math.abs(cr - board.goalRow) + Math.abs(cc - board.goalCol);
    }

    private double h3(GameState s, Board board) {
        double dr=s.row - board.goalRow, dc=s.col - board.goalCol;
        return Math.sqrt(dr * dr + dc * dc);
    }
}