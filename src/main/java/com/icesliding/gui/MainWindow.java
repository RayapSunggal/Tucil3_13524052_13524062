package com.icesliding.gui;

import com.icesliding.model.Board;
import com.icesliding.parser.InputParser;
import com.icesliding.parser.InputValidator;
import com.icesliding.parser.InputValidator.ValidationReport;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainWindow extends JFrame {
    private final FileDropZone fileDropZone=new FileDropZone();
    private final ValidationPanel validationPanel=new ValidationPanel();
    private final BoardPanel boardPanel=new BoardPanel();
    private final JLabel statusLabel=new JLabel("Validator siap");
    private final JLabel fileLabel=new JLabel("Belum ada file");

    public MainWindow() {
        super("Ice Sliding Puzzle Validator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 690));
        setContentPane(buildRoot());

        fileDropZone.setOnFile(this::loadFile);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildRoot() {
        JPanel root=new JPanel(new BorderLayout(0, 18));
        root.setBackground(UITheme.APP_BG);
        root.setBorder(new EmptyBorder(22, 24, 24, 24));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header=new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        JPanel titleBox=new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel title=new JLabel("Ice Sliding Puzzle");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle=new JLabel("GUI input validator");
        subtitle.setFont(UITheme.SUBTITLE);
        subtitle.setForeground(UITheme.INK_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitle);

        JPanel status=new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        status.setOpaque(false);
        status.add(new StatusPill(statusLabel));

        header.add(titleBox, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body=new JPanel(new BorderLayout(18, 0));
        body.setOpaque(false);
        body.add(buildSidebar(), BorderLayout.WEST);
        body.add(buildPreviewArea(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildSidebar() {
        JPanel sidebar=new JPanel();
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(322, 0));

        sidebar.add(buildPanel("Input", fileDropZone, 172));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildPanel("Validasi", validationPanel, 360));
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JPanel buildPreviewArea() {
        JPanel area=new JPanel(new BorderLayout(0, 12));
        area.setOpaque(false);

        JPanel top=new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);

        JPanel labels=new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel title=new JLabel("Preview Papan");
        title.setFont(UITheme.SECTION.deriveFont(15f));
        title.setForeground(UITheme.INK);
        fileLabel.setFont(UITheme.BODY);
        fileLabel.setForeground(UITheme.INK_MUTED);

        labels.add(title);
        labels.add(Box.createVerticalStrut(3));
        labels.add(fileLabel);

        top.add(labels, BorderLayout.WEST);
        top.add(buildLegend(), BorderLayout.EAST);

        JScrollPane scrollPane=new JScrollPane(boardPanel);
        scrollPane.setBorder(UITheme.lineBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(UITheme.SURFACE);

        area.add(top, BorderLayout.NORTH);
        area.add(scrollPane, BorderLayout.CENTER);
        return area;
    }

    private JPanel buildPanel(String title, Component content, int height) {
        JPanel panel=new RoundedPanel(UITheme.SURFACE);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(322, height));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        JLabel label=new JLabel(title.toUpperCase());
        label.setFont(UITheme.SMALL.deriveFont(11f));
        label.setForeground(UITheme.INK_FAINT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend=new JPanel(new GridLayout(1, 5, 8, 0));
        legend.setOpaque(false);
        legend.add(new LegendItem(UITheme.ICE, "Path"));
        legend.add(new LegendItem(UITheme.STONE, "Batu"));
        legend.add(new LegendItem(UITheme.RED_SOFT, "Lava"));
        legend.add(new LegendItem(UITheme.TEAL_SOFT, "Aktor"));
        legend.add(new LegendItem(UITheme.GREEN_SOFT, "Tujuan"));
        return legend;
    }

    private void loadFile(File file) {
        if (file==null){
            return;
        }

        if (!file.isFile()){
            handleInvalid(file, "File tidak ditemukan.");
            return;
        }
        if (!file.getName().toLowerCase().endsWith(".txt")){
            handleInvalid(file, "File input harus berekstensi .txt.");
            return;
        }

        List<String> lines;
        try{
            lines=InputParser.readLines(file.getAbsolutePath());
        }
        catch (IOException ex){
            fileDropZone.showInvalid("Gagal membaca file");
            validationPanel.showReadError(file, ex.getMessage());
            boardPanel.clearBoard();
            fileLabel.setText(file.getName());
            setStatus("Gagal membaca", UITheme.RED);
            return;
        }

        ValidationReport report=InputValidator.inspect(lines);
        if (!report.isValid()){
            handleInvalid(file, report.getMessage());
            return;
        }

        Board board=InputParser.parse(report.getNormalizedLines());
        boardPanel.setBoard(board);
        fileDropZone.showValid(file.getName(), report.getRows() + " x " + report.getCols() + " valid");
        validationPanel.showValid(file, report);
        fileLabel.setText(file.getName());
        setStatus("Input valid", UITheme.GREEN);
    }

    private void handleInvalid(File file, String message) {
        fileDropZone.showInvalid(message);
        validationPanel.showInvalid(file, message);
        boardPanel.clearBoard();
        fileLabel.setText(file.getName());
        setStatus("Input invalid", UITheme.RED);
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
        statusLabel.repaint();
    }

    private static final class RoundedPanel extends JPanel {
        private final Color fill;

        RoundedPanel(Color fill) {
            this.fill=fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
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

    private static final class StatusPill extends JPanel {
        private final JLabel text;

        StatusPill(JLabel text) {
            this.text=text;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(8, 14, 8, 14));
            text.setFont(UITheme.BODY_BOLD);
            text.setForeground(UITheme.TEAL);
            add(text, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UITheme.SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.setColor(UITheme.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class LegendItem extends JPanel {
        private final Color color;
        private final String label;

        LegendItem(Color color, String label) {
            this.color=color;
            this.label=label;
            setOpaque(false);
            setPreferredSize(new Dimension(74, 24));
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
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