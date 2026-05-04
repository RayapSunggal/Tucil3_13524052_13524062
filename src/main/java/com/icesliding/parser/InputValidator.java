package com.icesliding.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InputValidator {
    private static final String VALID_TILES="*XLZO0123456789";

    private InputValidator() {
    }

    public static String validate(List<String> lines) {
        ValidationReport report=inspect(lines);
        return report.isValid() ? null : report.getMessage();
    }

    public static ValidationReport inspect(List<String> rawLines) {
        List<String> lines=normalize(rawLines);
        if (lines.isEmpty()){
            return ValidationReport.invalid("File kosong.");
        }

        String[] dims=lines.get(0).trim().split("\\s+");
        if (dims.length!=2 || dims[0].isEmpty() || dims[1].isEmpty()){
            return ValidationReport.invalid("Baris pertama harus berisi tepat dua angka: N M.");
        }

        int rows;
        int cols;
        try{
            rows=Integer.parseInt(dims[0]);
            cols=Integer.parseInt(dims[1]);
        }
        catch (NumberFormatException ex){
            return ValidationReport.invalid("Dimensi N dan M harus berupa bilangan bulat positif.");
        }

        if (rows<=0 || cols<=0){
            return ValidationReport.invalid("Dimensi papan harus positif. Ditemukan N="+rows+", M="+cols+".");
        }

        int expectedLines=1+rows+rows;
        if (lines.size() < expectedLines){
            return ValidationReport.invalid("Jumlah baris kurang. Diperlukan "+expectedLines+
                    " baris, tersedia "+lines.size()+".");
        }
        if (lines.size() > expectedLines){
            return ValidationReport.invalid("Jumlah baris berlebih. Format membutuhkan tepat "+expectedLines+
                    " baris setelah mengabaikan baris kosong di akhir file.");
        }

        int startCount=0;
        int goalCount=0;
        int wallCount=0;
        int lavaCount=0;
        int pathCount=0;
        int[] checkpointCounts=new int[10];

        for (int r=0; r < rows; r++){
            String row=lines.get(1+r);
            if (row.length()!=cols){
                return ValidationReport.invalid("Baris papan ke-"+(r+1)+" memiliki panjang "+
                        row.length()+", seharusnya "+cols+".");
            }

            for (int c=0; c < cols; c++){
                char tile=row.charAt(c);
                if (VALID_TILES.indexOf(tile) < 0){
                    return ValidationReport.invalid("Karakter '"+tile+"' pada baris papan ke-"+(r+1)+
                            ", kolom ke-"+(c+1)+" tidak dikenali.");
                }

                if (tile=='Z'){
                    startCount++;
                }
                else if (tile=='O'){
                    goalCount++;
                }
                else if (tile=='X'){
                    wallCount++;
                }
                else if (tile=='L'){
                    lavaCount++;
                }
                else if (tile=='*'){
                    pathCount++;
                }
                else if (tile>='0' && tile<='9'){
                    checkpointCounts[tile-'0']++;
                }
            }
        }

        if (startCount!=1){
            return ValidationReport.invalid(startCount==0
                    ? "Titik awal Z wajib ada tepat satu."
                    : "Titik awal Z hanya boleh satu. Ditemukan "+startCount+".");
        }
        if (goalCount!=1){
            return ValidationReport.invalid(goalCount==0
                    ? "Titik tujuan O wajib ada tepat satu."
                    : "Titik tujuan O hanya boleh satu. Ditemukan "+goalCount+".");
        }

        int checkpointTotal=validateCheckpoints(checkpointCounts);
        if (checkpointTotal < 0){
            return ValidationReport.invalid(checkpointError(checkpointCounts));
        }

        int minCost=Integer.MAX_VALUE;
        int maxCost=Integer.MIN_VALUE;
        long costTotal=0L;
        for (int r=0; r < rows; r++){
            String line=lines.get(1+rows+r).trim();
            if (line.isEmpty()){
                return ValidationReport.invalid("Baris cost ke-"+(r+1)+" kosong.");
            }

            String[] parts=line.split("\\s+");
            if (parts.length!=cols){
                return ValidationReport.invalid("Baris cost ke-"+(r+1)+" memiliki "+parts.length+
                        " nilai, seharusnya "+cols+".");
            }

            for (int c=0; c < cols; c++){
                int value;
                try{
                    value=Integer.parseInt(parts[c]);
                }
                catch (NumberFormatException ex){
                    return ValidationReport.invalid("Nilai cost '"+parts[c]+"' pada baris cost ke-"+
                            (r+1)+", kolom ke-"+(c+1)+" bukan bilangan bulat.");
                }

                if (value < 0){
                    return ValidationReport.invalid("Cost pada baris ke-"+(r+1)+", kolom ke-"+
                            (c+1)+" tidak boleh negatif.");
                }

                minCost=Math.min(minCost, value);
                maxCost=Math.max(maxCost, value);
                costTotal+=value;
            }
        }

        return ValidationReport.valid(lines, rows, cols, checkpointTotal, wallCount, lavaCount,
                pathCount, minCost, maxCost, costTotal);
    }

    private static List<String> normalize(List<String> rawLines) {
        if (rawLines==null){
            return Collections.emptyList();
        }

        List<String> lines=new ArrayList<>(rawLines);
        while (!lines.isEmpty() && lines.get(lines.size()-1).trim().isEmpty()){
            lines.remove(lines.size()-1);
        }
        return lines;
    }

    private static int validateCheckpoints(int[] counts) {
        int total=0;
        int highest=-1;
        for (int i=0; i < counts.length; i++){
            if (counts[i] > 1){
                return -1;
            }
            if (counts[i]==1){
                highest=i;
                total++;
            }
        }

        for (int i=0; i<=highest; i++){
            if (counts[i]==0){
                return -1;
            }
        }
        return total;
    }

    private static String checkpointError(int[] counts) {
        for (int i=0; i < counts.length; i++){
            if (counts[i] > 1){
                return "Checkpoint "+i+" hanya boleh muncul satu kali. Ditemukan "+counts[i]+".";
            }
        }

        for (int i=0; i < counts.length; i++){
            if (counts[i]==0){
                for (int j=i+1; j < counts.length; j++){
                    if (counts[j]==1){
                        return "Checkpoint harus berurutan mulai dari 0. Digit "+j+
                                " ada, tetapi digit "+i+" tidak ada.";
                    }
                }
            }
        }
        return "Checkpoint tidak valid.";
    }

    public static final class ValidationReport {
        private final boolean valid;
        private final String message;
        private final List<String> normalizedLines;
        private final int rows;
        private final int cols;
        private final int checkpointCount;
        private final int wallCount;
        private final int lavaCount;
        private final int pathCount;
        private final int minCost;
        private final int maxCost;
        private final long costTotal;

        private ValidationReport(boolean valid, String message, List<String> normalizedLines,
                                 int rows, int cols, int checkpointCount, int wallCount,
                                 int lavaCount, int pathCount, int minCost, int maxCost,
                                 long costTotal) {
            this.valid=valid;
            this.message=message;
            this.normalizedLines=normalizedLines;
            this.rows=rows;
            this.cols=cols;
            this.checkpointCount=checkpointCount;
            this.wallCount=wallCount;
            this.lavaCount=lavaCount;
            this.pathCount=pathCount;
            this.minCost=minCost;
            this.maxCost=maxCost;
            this.costTotal=costTotal;
        }

        static ValidationReport invalid(String message) {
            return new ValidationReport(false, message, Collections.emptyList(), 0, 0, 0,
                    0, 0, 0, 0, 0, 0L);
        }

        static ValidationReport valid(List<String> lines, int rows, int cols, int checkpointCount,
                                      int wallCount, int lavaCount, int pathCount, int minCost,
                                      int maxCost, long costTotal) {
            return new ValidationReport(true, "Input valid.", Collections.unmodifiableList(new ArrayList<>(lines)),
                    rows, cols, checkpointCount, wallCount, lavaCount, pathCount,
                    minCost, maxCost, costTotal);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public List<String> getNormalizedLines() {
            return normalizedLines;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public int getCheckpointCount() {
            return checkpointCount;
        }

        public int getWallCount() {
            return wallCount;
        }

        public int getLavaCount() {
            return lavaCount;
        }

        public int getPathCount() {
            return pathCount;
        }

        public int getMinCost() {
            return minCost;
        }

        public int getMaxCost() {
            return maxCost;
        }

        public long getCostTotal() {
            return costTotal;
        }
    }
}