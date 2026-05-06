package com.icesliding.solver;

import com.icesliding.model.Board;
import com.icesliding.model.GameState;

import java.util.PriorityQueue;
import java.util.Comparator;

class UCSNode {
    private int row, col;
    private int lowestCost;
    
    public UCSNode(int row, int col, int lowestCost) {
        this.row = row;
        this.col = col;
        this.lowestCost = lowestCost;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getLowestCost() {
        return lowestCost;
    }
}


class UCSPriorityQueue {

    // Prio Q dengan lowestCost as comparator
    static PriorityQueue<UCSNode> createQueue() {
        return new PriorityQueue<>(Comparator.comparingInt(UCSNode::getLowestCost));
    }

    static boolean contains(PriorityQueue<UCSNode> pq, int row, int col) {
        for (UCSNode node : pq)
            if (node.getRow() == row && node.getCol() == col) return true;
        return false;
    }

    // Update cost if have lower cost than lowerCost in queue
    static void decreaseKey(PriorityQueue<UCSNode> pq, int row, int col, int newCost) {
        for (UCSNode node : pq) {
            if (node.getRow() == row && node.getCol() == col) {
                if (newCost < node.getLowestCost()) {
                    pq.remove(node);                            // remove stale entry
                    pq.offer(new UCSNode(row, col, newCost));  // re-insert with new cost
                }
                return;
            }
        }
    }


}
public class UCS {
    private PriorityQueue<UCSNode> checkList;
    private boolean[][] visited;
    private boolean goal = false;

    public SolverResult solve(Board board) {
        long t0 = System.currentTimeMillis();
        //initialization
        GameState initial = new GameState(board.startRow, board.startCol, 0, 0, null, '\0');
        checkList = UCSPriorityQueue.createQueue();
        visited = new boolean[board.rows][board.cols];
        checkList.offer(new UCSNode(initial.row, initial.col, initial.gCost));
        int nodesVisited = 0;

        // loop until semua node di search
        while (!checkList.isEmpty()) {
            UCSNode currNode = checkList.poll();
            int row = currNode.getRow();
            int col = currNode.getCol();

            if (visited[row][col]) continue;
            visited[row][col] = true;
            nodesVisited++;
            //cek goal
            if (row == board.goalRow && col == board.goalCol) {
                goal = true;
                break;
            }
            // coba semua slide, extract newPos dan newCost, insert ke prio queue if not yet visited
            for (int d = 0; d < SlideSimulator.dirCount(); d++) {
                int[] slide = SlideSimulator.slide(board, row, col, d, 0);
                if (slide == null) continue;

                int newRow = slide[0];
                int newCol = slide[1];
                int newCost = currNode.getLowestCost() + slide[2];

                if (!visited[newRow][newCol]) {
                    if (!UCSPriorityQueue.contains(checkList, newRow, newCol)) {
                        checkList.offer(new UCSNode(newRow, newCol, newCost));
                    } else {
                        UCSPriorityQueue.decreaseKey(checkList, newRow, newCol, newCost);
                    }
                }
            }
        }

        return goal ? SolverResult.found(null, nodesVisited, System.currentTimeMillis() - t0)
                    : SolverResult.notFound(nodesVisited, System.currentTimeMillis() - t0);
    }
}
