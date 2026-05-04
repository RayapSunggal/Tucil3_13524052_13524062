package com.icesliding.parser;

import com.icesliding.model.Board;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class InputParser {

    public static List<String> readLines(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
    }
    public static Board parse(List<String> lines) {
        String[] dims=lines.get(0).trim().split("\\s+");
        int N=Integer.parseInt(dims[0]);
        int M=Integer.parseInt(dims[1]);

        Board board=new Board(N, M);
        for (int i=0; i < N; i++){
            String row=lines.get(1+i);
            for (int j=0; j < M; j++){
                char c=row.charAt(j);
                board.grid[i][j]=c;
                if (c=='Z'){
                    board.startRow=i;
                    board.startCol=j;
                }
                if (c=='O'){
                    board.goalRow=i;
                    board.goalCol=j;
                }
                if (c>='0' && c<='9'){
                    int num=c-'0';
                    board.checkpointPos[num][0]=i;
                    board.checkpointPos[num][1]=j;
                    if (num > board.maxCheckpoint){
                        board.maxCheckpoint=num;
                    }
                }
            }
        }
        for (int i=0; i < N; i++){
            String[] parts=lines.get(1+N+i).trim().split("\\s+");
            for (int j=0; j < M; j++){
                board.cost[i][j]=Integer.parseInt(parts[j]);
            }
        }

        return board;
    }
}