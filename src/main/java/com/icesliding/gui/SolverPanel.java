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
import java.util.function.Consumer;

class SolverPanel extends JPanel {

    private final ButtonGroup  hGroup=new ButtonGroup();
    private final JRadioButton h1Btn=new JRadioButton("H1 – Manhattan ke tujuan");
    private final JRadioButton h2Btn=new JRadioButton("H2 – via checkpoint");
    private final JRadioButton h3Btn=new JRadioButton("H3 – Euclidean ke tujuan");
    private final JButton      solveBtn=new JButton("Cari Solusi (A*)");
    private final JLabel       statusLbl=new JLabel(" ");

    private Consumer<Integer> onSolve;

    SolverPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());


        JLabel hLabel=new JLabel("Heuristik:");
        hLabel.setFont(UITheme.BODY_BOLD);
        hLabel.setForeground(UITheme.INK);


        hGroup.add(h1Btn); hGroup.add(h2Btn); hGroup.add(h3Btn);
        h1Btn.setSelected(true);
        styleRadio(h1Btn); styleRadio(h2Btn); styleRadio(h3Btn);

        JPanel radioBox=new JPanel();
        radioBox.setOpaque(false);
        radioBox.setLayout(new BoxLayout(radioBox, BoxLayout.Y_AXIS));
        radioBox.add(h1Btn);
        radioBox.add(h2Btn);
        radioBox.add(h3Btn);


        solveBtn.setFont(UITheme.BODY_BOLD);
        solveBtn.setBackground(UITheme.TEAL);
        solveBtn.setForeground(Color.WHITE);
        solveBtn.setOpaque(true);
        solveBtn.setBorderPainted(false);
        solveBtn.setFocusPainted(false);
        solveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        solveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 34));
        solveBtn.setEnabled(false);
        solveBtn.addActionListener(e -> { if (onSolve!=null) onSolve.accept(selectedHeuristic()); });

        JPanel btnRow=new JPanel(new BorderLayout());
        btnRow.setOpaque(false);
        btnRow.add(solveBtn, BorderLayout.CENTER);


        statusLbl.setFont(UITheme.SMALL);
        statusLbl.setForeground(UITheme.INK_MUTED);

        JPanel statusRow=new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusRow.setOpaque(false);
        statusRow.add(statusLbl);


        JPanel content=new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(hLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(radioBox);
        content.add(Box.createVerticalStrut(10));
        content.add(statusRow);
        content.add(Box.createVerticalStrut(8));
        content.add(btnRow);

        setBorder(new EmptyBorder(0, 0, 0, 0));
        add(content, BorderLayout.NORTH);
    }

    void setOnSolve(Consumer<Integer> callback) { this.onSolve=callback; }

    void setFileLoaded(boolean loaded) {
        solveBtn.setEnabled(loaded);
        if (!loaded) setStatus("Muat file .txt terlebih dahulu.", UITheme.INK_MUTED);
        else         setStatus("Pilih heuristik dan tekan Cari Solusi.", UITheme.INK_MUTED);
    }

    void setSolving(boolean solving) {
        solveBtn.setEnabled(!solving);
        h1Btn.setEnabled(!solving);
        h2Btn.setEnabled(!solving);
        h3Btn.setEnabled(!solving);
        if (solving) setStatus("Mencari solusi...", UITheme.AMBER);
    }

    void setStatus(String text, Color color) {
        statusLbl.setText(text);
        statusLbl.setForeground(color);
        statusLbl.repaint();
    }

    int selectedHeuristic() {
        if (h2Btn.isSelected()) return 2;
        if (h3Btn.isSelected()) return 3;
        return 1;
    }

    private static void styleRadio(JRadioButton btn) {
        btn.setOpaque(false);
        btn.setFont(UITheme.BODY);
        btn.setForeground(UITheme.INK);
        btn.setFocusPainted(false);
    }
}