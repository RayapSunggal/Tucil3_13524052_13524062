package com.icesliding.gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.BiConsumer;

class SolverPanel extends JPanel {

    // algorithm: 0=UCS, 1=A*, 2=GBFS, 3=DFS
    private final ButtonGroup  algGroup = new ButtonGroup();
    private final JRadioButton ucsBtn   = new JRadioButton("UCS");
    private final JRadioButton astarBtn = new JRadioButton("A*");
    private final JRadioButton gbfsBtn  = new JRadioButton("GBFS");
    private final JRadioButton bfBtn    = new JRadioButton("DFS");

    private final ButtonGroup  hGroup = new ButtonGroup();
    private final JRadioButton h1Btn  = new JRadioButton("H1 – Manhattan via checkpoint");
    private final JRadioButton h2Btn  = new JRadioButton("H2 – Euclidean via checkpoint");
    private final JRadioButton h3Btn  = new JRadioButton("H3 – Chebyshev via checkpoint");
    private final JRadioButton h4Btn  = new JRadioButton("H4 – Minkowski (p=3) via checkpoint");

    private final JLabel  hLabel   = new JLabel("Heuristik:");
    private final JPanel  radioBox;

    private final JButton solveBtn  = new JButton("Cari Solusi");
    private final JLabel  statusLbl = new JLabel(" ");

    // callback(algorithm, heuristic): algorithm 0=UCS,1=A*,2=GBFS,3=DFS; heuristic 1/2/3/4
    private BiConsumer<Integer, Integer> onSolve;

    SolverPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // --- Algorithm row ---
        algGroup.add(ucsBtn); algGroup.add(astarBtn); algGroup.add(gbfsBtn); algGroup.add(bfBtn);
        astarBtn.setSelected(true);
        styleRadio(ucsBtn); styleRadio(astarBtn); styleRadio(gbfsBtn); styleRadio(bfBtn);

        JLabel algLabel = new JLabel("Algoritma:");
        algLabel.setFont(UITheme.BODY_BOLD);
        algLabel.setForeground(UITheme.INK);

        JPanel algRow = new JPanel(new GridLayout(2, 2, 6, 2));
        algRow.setOpaque(false);
        algRow.add(ucsBtn);
        algRow.add(astarBtn);
        algRow.add(gbfsBtn);
        algRow.add(bfBtn);

        // --- Heuristic section ---
        hGroup.add(h1Btn); hGroup.add(h2Btn); hGroup.add(h3Btn); hGroup.add(h4Btn);
        h1Btn.setSelected(true);
        styleRadio(h1Btn); styleRadio(h2Btn); styleRadio(h3Btn); styleRadio(h4Btn);

        hLabel.setFont(UITheme.BODY_BOLD);
        hLabel.setForeground(UITheme.INK);

        radioBox = new JPanel();
        radioBox.setOpaque(false);
        radioBox.setLayout(new BoxLayout(radioBox, BoxLayout.Y_AXIS));
        radioBox.add(h1Btn);
        radioBox.add(h2Btn);
        radioBox.add(h3Btn);
        radioBox.add(h4Btn);

        // Toggle heuristic visibility based on algorithm
        ucsBtn.addActionListener(e -> updateHeuristicVisibility());
        astarBtn.addActionListener(e -> updateHeuristicVisibility());
        gbfsBtn.addActionListener(e -> updateHeuristicVisibility());
        bfBtn.addActionListener(e -> updateHeuristicVisibility());

        // --- Solve button ---
        solveBtn.setFont(UITheme.BODY_BOLD);
        solveBtn.setBackground(UITheme.TEAL);
        solveBtn.setForeground(Color.WHITE);
        solveBtn.setOpaque(true);
        solveBtn.setBorderPainted(false);
        solveBtn.setFocusPainted(false);
        solveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        solveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 34));
        solveBtn.setEnabled(false);
        solveBtn.addActionListener(e -> {
            if (onSolve != null) onSolve.accept(selectedAlgorithm(), selectedHeuristic());
        });

        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.add(solveBtn, BorderLayout.CENTER);

        // --- Status ---
        statusLbl.setFont(UITheme.SMALL);
        statusLbl.setForeground(UITheme.INK_MUTED);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.add(statusLbl);

        // --- Assemble ---
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(algLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(algRow);
        content.add(Box.createVerticalStrut(8));
        content.add(hLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(radioBox);
        content.add(Box.createVerticalStrut(8));
        content.add(statusRow);
        content.add(Box.createVerticalStrut(6));
        content.add(btnRow);

        setBorder(new EmptyBorder(0, 0, 0, 0));
        add(content, BorderLayout.NORTH);
    }

    private void updateHeuristicVisibility() {
        boolean needsH = astarBtn.isSelected() || gbfsBtn.isSelected();
        hLabel.setVisible(needsH);
        radioBox.setVisible(needsH);
        String label = ucsBtn.isSelected()   ? "Cari Solusi (UCS)"
                     : astarBtn.isSelected() ? "Cari Solusi (A*)"
                     : gbfsBtn.isSelected()  ? "Cari Solusi (GBFS)"
                     : "Cari Solusi (DFS)";
        solveBtn.setText(label);
        revalidate();
        repaint();
    }

    void setOnSolve(BiConsumer<Integer, Integer> callback) { this.onSolve = callback; }

    void setFileLoaded(boolean loaded) {
        solveBtn.setEnabled(loaded);
        if (!loaded) setStatus("Muat file .txt terlebih dahulu.", UITheme.INK_MUTED);
        else         setStatus("Pilih algoritma dan tekan Cari Solusi.", UITheme.INK_MUTED);
    }

    void setSolving(boolean solving) {
        solveBtn.setEnabled(!solving);
        ucsBtn.setEnabled(!solving); astarBtn.setEnabled(!solving);
        gbfsBtn.setEnabled(!solving); bfBtn.setEnabled(!solving);
        h1Btn.setEnabled(!solving); h2Btn.setEnabled(!solving);
        h3Btn.setEnabled(!solving); h4Btn.setEnabled(!solving);
        if (solving) setStatus("Mencari solusi...", UITheme.AMBER);
    }

    void setStatus(String text, Color color) {
        statusLbl.setText(text);
        statusLbl.setForeground(color);
        statusLbl.repaint();
    }

    int selectedAlgorithm() {
        if (ucsBtn.isSelected())  return 0;
        if (gbfsBtn.isSelected()) return 2;
        if (bfBtn.isSelected())   return 3;
        return 1; // A*
    }

    int selectedHeuristic() {
        if (h2Btn.isSelected()) return 2;
        if (h3Btn.isSelected()) return 3;
        if (h4Btn.isSelected()) return 4;
        return 1;
    }

    private static void styleRadio(JRadioButton btn) {
        btn.setOpaque(false);
        btn.setFont(UITheme.BODY);
        btn.setForeground(UITheme.INK);
        btn.setFocusPainted(false);
    }
}
