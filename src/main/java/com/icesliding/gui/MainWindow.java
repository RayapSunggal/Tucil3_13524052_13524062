package com.icesliding.gui;

import com.icesliding.model.Board;
import com.icesliding.parser.InputParser;
import com.icesliding.parser.InputValidator;
import com.icesliding.parser.InputValidator.ValidationReport;
import com.icesliding.solver.Astar;
import com.icesliding.solver.DFS;
import com.icesliding.solver.GBFS;
import com.icesliding.solver.UCS;
import com.icesliding.solver.SolverResult;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

    private final FileDropZone fileDropZone = new FileDropZone();
    private final SolverPanel  solverPanel  = new SolverPanel();

    private final BoardPanel boardPanel = new BoardPanel();
    private final JLabel     fileLabel  = new JLabel("Belum ada file");

    private Board                           currentBoard;
    private SolverResult                    currentResult;
    private SwingWorker<SolverResult, Void> currentWorker;
    private int     playbackStep = 0;
    private boolean isPlaying    = false;
    private Timer   playbackTimer;

    private JPanel solverResultArea;
    private JLabel movesDisplayLabel;
    private JLabel costDisplayLabel;
    private JLabel nodesDisplayLabel;
    private JLabel timeDisplayLabel;
    private JLabel stepLabel;
    private JButton prevBtn, nextBtn, playPauseBtn, saveBtn;
    private JSlider speedSlider;

    public MainWindow() {
        super("Ice Sliding Puzzle Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 720));
        setContentPane(buildRoot());

        fileDropZone.setOnFile(this::loadFile);
        solverPanel.setOnSolve(this::runSolver);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UITheme.APP_BG);
        root.setBorder(new EmptyBorder(22, 24, 24, 24));
        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildBody(),    BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Ice Sliding Puzzle Solver");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("UCS / A* / GBFS / DFS Pathfinding");
        subtitle.setFont(UITheme.SUBTITLE);
        subtitle.setForeground(UITheme.INK_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setOpaque(false);
        body.add(buildSidebar(),     BorderLayout.WEST);
        body.add(buildPreviewArea(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(340, 0));

        sidebar.add(buildPanel("Input",  fileDropZone, 155));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(buildPanel("Solver", solverPanel,  340));
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel buildPreviewArea() {
        JPanel area = new JPanel(new BorderLayout(0, 10));
        area.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Preview Papan");
        title.setFont(UITheme.SECTION.deriveFont(15f));
        title.setForeground(UITheme.INK);
        fileLabel.setFont(UITheme.BODY);
        fileLabel.setForeground(UITheme.INK_MUTED);
        labels.add(title);
        labels.add(Box.createVerticalStrut(3));
        labels.add(fileLabel);

        top.add(labels,        BorderLayout.WEST);
        top.add(buildLegend(), BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(boardPanel);
        scrollPane.setBorder(UITheme.lineBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(UITheme.SURFACE);

        solverResultArea = buildSolverResultArea();

        area.add(top,              BorderLayout.NORTH);
        area.add(scrollPane,       BorderLayout.CENTER);
        area.add(solverResultArea, BorderLayout.SOUTH);
        return area;
    }

    private JPanel buildSolverResultArea() {
        JPanel area = new JPanel(new BorderLayout(0, 6));
        area.setOpaque(false);
        area.setVisible(false);

        movesDisplayLabel = new JLabel("-");
        costDisplayLabel  = new JLabel("-");
        nodesDisplayLabel = new JLabel("-");
        timeDisplayLabel  = new JLabel("-");

        JPanel infoStrip = new RoundedPanel(UITheme.SURFACE);
        infoStrip.setLayout(new GridLayout(1, 4, 1, 0));
        infoStrip.add(makeInfoBox("Solusi",  movesDisplayLabel));
        infoStrip.add(makeInfoBox("Cost",    costDisplayLabel));
        infoStrip.add(makeInfoBox("Iterasi", nodesDisplayLabel));
        infoStrip.add(makeInfoBox("Waktu",   timeDisplayLabel));
        infoStrip.setPreferredSize(new Dimension(0, 58));

        prevBtn      = makeCtrlBtn("◄");
        nextBtn      = makeCtrlBtn("►");
        playPauseBtn = makeCtrlBtn("▶ Play");
        saveBtn      = makeCtrlBtn("Simpan .txt");
        saveBtn.setBackground(new Color(0x2F9E70));
        saveBtn.setForeground(Color.WHITE);

        stepLabel = new JLabel("Step 0 / 0");
        stepLabel.setFont(UITheme.BODY_BOLD);
        stepLabel.setForeground(UITheme.INK);

        speedSlider = new JSlider(1, 10, 1);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(100, 24));

        JLabel speedLbl = new JLabel("Kecepatan:");
        speedLbl.setFont(UITheme.SMALL);
        speedLbl.setForeground(UITheme.INK_MUTED);

        JPanel playbackStrip = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        playbackStrip.setOpaque(false);
        playbackStrip.add(prevBtn);
        playbackStrip.add(stepLabel);
        playbackStrip.add(nextBtn);
        playbackStrip.add(playPauseBtn);
        playbackStrip.add(speedLbl);
        playbackStrip.add(speedSlider);
        playbackStrip.add(saveBtn);

        prevBtn.addActionListener(e      -> updatePlaybackStep(playbackStep - 1));
        nextBtn.addActionListener(e      -> updatePlaybackStep(playbackStep + 1));
        playPauseBtn.addActionListener(e -> togglePlay());
        saveBtn.addActionListener(e      -> saveSolution());

        area.add(infoStrip,     BorderLayout.CENTER);
        area.add(playbackStrip, BorderLayout.SOUTH);
        return area;
    }

    private JPanel buildPanel(String title, Component content, int height) {
        JPanel panel = new RoundedPanel(UITheme.SURFACE);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(340, height));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        JLabel label = new JLabel(title.toUpperCase());
        label.setFont(UITheme.SMALL.deriveFont(11f));
        label.setForeground(UITheme.INK_FAINT);
        panel.add(label,   BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new GridLayout(1, 7, 8, 0));
        legend.setOpaque(false);
        legend.add(new LegendItem(UITheme.ICE,              "Path"));
        legend.add(new LegendItem(new Color(0x7BE38A),      "Solusi"));
        legend.add(new LegendItem(UITheme.STONE,            "Batu"));
        legend.add(new LegendItem(UITheme.LAVA_SOFT,        "Lava"));
        legend.add(new LegendItem(UITheme.AMBER_SOFT,       "Checkpoint"));
        legend.add(new LegendItem(UITheme.ACTOR_SOFT,       "Aktor"));
        legend.add(new LegendItem(UITheme.GOAL_SOFT,        "Tujuan"));
        return legend;
    }

    private JPanel makeInfoBox(String label, JLabel valueLabel) {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(UITheme.SMALL.deriveFont(10f));
        lbl.setForeground(UITheme.INK_FAINT);

        valueLabel.setFont(UITheme.MONO.deriveFont(Font.BOLD, 12f));
        valueLabel.setForeground(UITheme.INK);

        box.add(lbl);
        box.add(Box.createVerticalStrut(3));
        box.add(valueLabel);
        return box;
    }

    private JButton makeCtrlBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.BODY_BOLD);
        btn.setBackground(UITheme.SURFACE_ALT);
        btn.setForeground(UITheme.INK);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadFile(File file) {
        if (file == null) return;
        if (currentWorker != null && !currentWorker.isDone()) currentWorker.cancel(true);

        if (!file.isFile()) {
            handleInvalid("File tidak ditemukan.");
            fileLabel.setText(file.getName());
            return;
        }
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            handleInvalid("File input harus berekstensi .txt.");
            fileLabel.setText(file.getName());
            return;
        }

        List<String> lines;
        try {
            lines = InputParser.readLines(file.getAbsolutePath());
        } catch (IOException ex) {
            fileDropZone.showInvalid("Gagal membaca file: " + ex.getMessage());
            boardPanel.clearBoard();
            fileLabel.setText(file.getName());
            resetSolverState();
            return;
        }

        ValidationReport report = InputValidator.inspect(lines);
        if (!report.isValid()) {
            handleInvalid(report.getMessage());
            fileDropZone.showInvalid(report.getMessage());
            fileLabel.setText(file.getName());
            return;
        }

        Board board = InputParser.parse(report.getNormalizedLines());
        currentBoard = board;
        boardPanel.setBoard(board);
        fileDropZone.showValid(file.getName(), report.getRows() + " x " + report.getCols() + " valid");
        fileLabel.setText(file.getName());
        resetSolverState();
        solverPanel.setFileLoaded(true);
    }

    private void handleInvalid(String message) {
        currentBoard = null;
        boardPanel.clearBoard();
        resetSolverState();
        solverPanel.setFileLoaded(false);
        solverPanel.setStatus("Input tidak valid: " + message, UITheme.RED);
    }

    private void resetSolverState() {
        stopPlayback();
        currentResult = null;
        playbackStep  = 0;
        boardPanel.clearPlayback();
        if (solverResultArea != null) solverResultArea.setVisible(false);
        solverPanel.setStatus("Muat file .txt terlebih dahulu.", UITheme.INK_MUTED);
        revalidate();
        repaint();
    }

    private void runSolver(int algorithm, int heuristic) {
        if (currentBoard == null) {
            solverPanel.setStatus("Muat file .txt terlebih dahulu.", UITheme.RED);
            return;
        }

        stopPlayback();
        solverPanel.setSolving(true);
        solverResultArea.setVisible(false);
        boardPanel.setBoard(currentBoard);

        Board boardSnapshot = currentBoard;

        currentWorker = new SwingWorker<SolverResult, Void>() {
            @Override protected SolverResult doInBackground() {
                switch (algorithm) {
                    case 0:  return new UCS().solve(boardSnapshot);
                    case 2:  return new GBFS(heuristic).solve(boardSnapshot);
                    case 3:  return new DFS().solve(boardSnapshot);
                    default: return new Astar(heuristic).solve(boardSnapshot);
                }
            }
            @Override protected void done() {
                if (isCancelled()) return;
                try {
                    onSolverComplete(get());
                } catch (Exception ex) {
                    solverPanel.setStatus("Error: " + ex.getMessage(), UITheme.RED);
                } finally {
                    solverPanel.setSolving(false);
                }
            }
        };
        currentWorker.execute();
    }

    private void onSolverComplete(SolverResult result) {
        currentResult = result;
        if (!result.found) {
            solverPanel.setStatus(
                    "Solusi tidak ditemukan. Iterasi: " + result.nodesVisited, UITheme.RED);
            return;
        }

        String movesStr = result.moves.stream()
                .map(String::valueOf).collect(Collectors.joining(""));

        solverPanel.setStatus(
                "Ditemukan! " + result.moves.size() + " langkah, cost " + result.cost,
                UITheme.GREEN);

        movesDisplayLabel.setText(movesStr.isEmpty() ? "(kosong)" : movesStr);
        costDisplayLabel.setText(String.valueOf(result.cost));
        nodesDisplayLabel.setText(String.valueOf(result.nodesVisited));
        timeDisplayLabel.setText(result.timeMs + " ms");

        boardPanel.setPlayback(currentBoard, result.positions, result.checkpointsAtStep, result.moves);
        playbackStep = 0;
        solverResultArea.setVisible(true);
        updatePlaybackStep(0);

        revalidate();
        repaint();
    }


    private void updatePlaybackStep(int step) {
        if (currentResult == null || !currentResult.found) return;
        int maxStep = currentResult.positions.size() - 1;
        step = Math.max(0, Math.min(step, maxStep));
        playbackStep = step;
        boardPanel.setStep(currentResult.positions, currentResult.checkpointsAtStep, step);

        String moveTag = (step == 0) ? "Initial"
                : "Step " + step + " : " + currentResult.moves.get(step - 1);
        stepLabel.setText(moveTag + "   (" + step + " / " + maxStep + ")");
        prevBtn.setEnabled(step > 0);
        nextBtn.setEnabled(step < maxStep);
    }

    private void togglePlay() {
        if (isPlaying) {
            stopPlayback();
        } else {
            if (currentResult == null || !currentResult.found) return;
            if (playbackStep >= currentResult.positions.size() - 1) updatePlaybackStep(0);
            isPlaying = true;
            playPauseBtn.setText("⏸ Pause");
            advancePlaybackStep();
        }
    }

    private void advancePlaybackStep() {
        if (!isPlaying || currentResult == null) return;
        if (playbackStep >= currentResult.positions.size() - 1) {
            stopPlayback();
            return;
        }
        int nextStep = playbackStep + 1;
        int animMs = boardPanel.stepDurationMs(currentResult.positions, nextStep);
        int userGap = 1100 - speedSlider.getValue() * 100;
        int delay = animMs + userGap;
        updatePlaybackStep(nextStep);
        playbackTimer = new Timer(delay, null);
        playbackTimer.setRepeats(false);
        playbackTimer.addActionListener(e -> {
            if (!isPlaying) return;
            advancePlaybackStep();
        });
        playbackTimer.start();
    }

    private void stopPlayback() {
        isPlaying = false;
        if (playbackTimer != null) { playbackTimer.stop(); playbackTimer = null; }
        if (playPauseBtn  != null) playPauseBtn.setText("▶ Play");
    }


    private void saveSolution() {
        if (currentResult == null || !currentResult.found) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Solusi");
        chooser.setSelectedFile(new File("solusi.txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
            String movesStr = currentResult.moves.stream()
                    .map(String::valueOf).collect(Collectors.joining(""));

            pw.println("Solusi Yang Ditemukan : " + movesStr);
            pw.println("Cost dari Solusi      : " + currentResult.cost);
            pw.println("Waktu eksekusi        : " + currentResult.timeMs + " ms");
            pw.println("Banyak iterasi        : " + currentResult.nodesVisited + " iterasi");
            pw.println();

            for (int i = 0; i < currentResult.positions.size(); i++) {
                if (i == 0) pw.println("Initial");
                else        pw.println("Step " + i + " : " + currentResult.moves.get(i - 1));
                int[] pos = currentResult.positions.get(i);
                int   cp  = currentResult.checkpointsAtStep.get(i);
                pw.print(boardToString(currentBoard, pos[0], pos[1], cp));
                pw.println();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String boardToString(Board board, int actorRow, int actorCol, int nextCheckpoint) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < board.rows; r++) {
            for (int c = 0; c < board.cols; c++) {
                if (r == actorRow && c == actorCol) {
                    sb.append('Z');
                } else {
                    char tile = board.grid[r][c];
                    if (tile == 'Z') {
                        sb.append('*');
                    } else if (tile >= '0' && tile <= '9' && (tile - '0') < nextCheckpoint) {
                        sb.append('*');
                    } else {
                        sb.append(tile);
                    }
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    static final class RoundedPanel extends JPanel {
        private final Color fill;

        RoundedPanel(Color fill) {
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 12));
            g2.fill(new RoundRectangle2D.Float(0, 4, getWidth(), getHeight() - 4, 10, 10));
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() - 4, 8, 8));
            g2.setColor(UITheme.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 5f, 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class LegendItem extends JPanel {
        private final Color  color;
        private final String label;

        LegendItem(Color color, String label) {
            this.color = color;
            this.label = label;
            setOpaque(false);
            setPreferredSize(new Dimension(88, 24));
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(0, 5, 14, 14, 4, 4));
            g2.setColor(UITheme.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 5.5f, 13, 13, 4, 4));
            g2.setFont(UITheme.SMALL);
            g2.setColor(UITheme.INK_MUTED);
            g2.drawString(label, 20, 16);
            g2.dispose();
        }
    }
}
