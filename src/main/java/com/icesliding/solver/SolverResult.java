package com.icesliding.solver;

import com.icesliding.model.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SolverResult {
    public final boolean         found;
    public final int             cost;
    public final List<Character> moves;
    public final List<int[]>     positions;
    public final List<Integer>   checkpointsAtStep;
    public final int             nodesVisited;
    public final long            timeMs;

    private SolverResult(boolean found, int cost,
                         List<Character> moves, List<int[]> positions,
                         List<Integer> checkpointsAtStep,
                         int nodesVisited, long timeMs) {
        this.found=found;
        this.cost=cost;
        this.moves=moves;
        this.positions=positions;
        this.checkpointsAtStep=checkpointsAtStep;
        this.nodesVisited=nodesVisited;
        this.timeMs=timeMs;
    }

    static SolverResult found(GameState goal, int nodesVisited, long timeMs) {
        List<Character> moves=new ArrayList<>();
        List<int[]>     positions=new ArrayList<>();
        List<Integer>   checkpoints=new ArrayList<>();

        for (GameState s=goal; s!=null; s=s.parent) {
            positions.add(0, new int[]{s.row, s.col});
            checkpoints.add(0, s.nextCheckpoint);
            if (s.move!='\0') moves.add(0, s.move);
        }
        return new SolverResult(true, goal.gCost, moves, positions, checkpoints, nodesVisited, timeMs);
    }

    static SolverResult notFound(int nodesVisited, long timeMs) {
        return new SolverResult(false, -1,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                nodesVisited, timeMs);
    }
}