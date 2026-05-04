package com.icesliding.model;

import java.util.Objects;

public class GameState {
    public int row, col;

    public int nextCheckpoint;
    public int gCost;
    public GameState parent;
    public char move;

    public GameState(int row, int col, int nextCheckpoint, int gCost, GameState parent, char move) {
        this.row=row;
        this.col=col;
        this.nextCheckpoint=nextCheckpoint;
        this.gCost=gCost;
        this.parent=parent;
        this.move=move;
    }

    public String key() {
        return row+","+col+","+nextCheckpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameState)) return false;
        GameState s=(GameState) o;
        return row==s.row && col==s.col && nextCheckpoint==s.nextCheckpoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col, nextCheckpoint);
    }
}