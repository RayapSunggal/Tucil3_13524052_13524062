package com.icesliding.gui;

import com.icesliding.parser.InputValidator.ValidationReport;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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

class ValidationPanel extends JPanel {
    private final StatusDot statusDot=new StatusDot(UITheme.INK_FAINT);
    private final JLabel title=new JLabel("Belum ada input");
    private final JTextArea message=new JTextArea("Validator siap membaca file .txt.");
    private final JPanel metrics=new JPanel(new GridLayout(0, 2, 8, 8));

    ValidationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel header=new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        header.setOpaque(false);
        title.setFont(UITheme.SECTION);
        title.setForeground(UITheme.INK);
        header.add(statusDot);
        header.add(title);

        message.setOpaque(false);
        message.setEditable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(UITheme.BODY);
        message.setForeground(UITheme.INK_MUTED);
        message.setBorder(BorderFactory.createEmptyBorder());
        message.setFocusable(false);

        metrics.setOpaque(false);

        JPanel content=new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(header);
        content.add(Box.createVerticalStrut(10));
        content.add(message);
        content.add(Box.createVerticalStrut(16));
        content.add(metrics);

        add(content, BorderLayout.NORTH);
        showEmpty();
    }

    void showEmpty() {
        statusDot.setColor(UITheme.INK_FAINT);
        title.setText("Belum ada input");
        message.setText("Validator siap membaca file .txt.");
        metrics.removeAll();
        addMetric("Format", "N M + papan + cost");
        addMetric("Status", "Menunggu file");
        refresh();
    }

    void showValid(File file, ValidationReport report) {
        statusDot.setColor(UITheme.GREEN);
        title.setText("Input valid");
        message.setText(file.getName()+" sudah sesuai format spesifikasi.");
        metrics.removeAll();
        addMetric("Ukuran", report.getRows()+" x "+report.getCols());
        addMetric("Checkpoint", report.getCheckpointCount()==0 ? "Tidak ada" : report.getCheckpointCount()+" titik");
        addMetric("Batu", String.valueOf(report.getWallCount()));
        addMetric("Lava", String.valueOf(report.getLavaCount()));
        addMetric("Path", String.valueOf(report.getPathCount()));
        addMetric("Cost", report.getMinCost()+" - "+report.getMaxCost());
        refresh();
    }

    void showInvalid(File file, String error) {
        statusDot.setColor(UITheme.RED);
        title.setText("Input perlu diperbaiki");
        message.setText(file.getName()+": "+error);
        metrics.removeAll();
        addMetric("Status", "Gagal validasi");
        addMetric("Preview", "Dikosongkan");
        refresh();
    }

    void showReadError(File file, String error) {
        statusDot.setColor(UITheme.RED);
        title.setText("File tidak bisa dibaca");
        message.setText(file.getName()+": "+error);
        metrics.removeAll();
        addMetric("Status", "Gagal dibuka");
        addMetric("Preview", "Dikosongkan");
        refresh();
    }

    private void addMetric(String label, String value) {
        metrics.add(new MetricBox(label, value));
    }

    private void refresh() {
        revalidate();
        repaint();
    }

    private static final class MetricBox extends JPanel {
        private final String label;
        private final String value;

        MetricBox(String label, String value) {
            this.label=label;
            this.value=value;
            setOpaque(false);
            setPreferredSize(new Dimension(132, 58));
            setMinimumSize(new Dimension(118, 58));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UITheme.SURFACE_ALT);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.setColor(UITheme.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1f, getHeight()-1f, 8, 8));

            g2.setFont(UITheme.SMALL);
            g2.setColor(UITheme.INK_FAINT);
            g2.drawString(label, 10, 20);

            g2.setFont(UITheme.BODY_BOLD);
            g2.setColor(UITheme.INK);
            g2.drawString(fit(value, g2, getWidth()-20), 10, 40);
            g2.dispose();
        }

        private String fit(String text, Graphics2D g2, int width) {
            if (g2.getFontMetrics().stringWidth(text)<=width){
                return text;
            }
            String clipped=text;
            while (clipped.length() > 3 && g2.getFontMetrics().stringWidth(clipped+"...") > width){
                clipped=clipped.substring(0, clipped.length()-1);
            }
            return clipped+"...";
        }
    }

    private static final class StatusDot extends Component {
        private Color color;

        StatusDot(Color color) {
            this.color=color;
            setPreferredSize(new Dimension(12, 18));
        }

        void setColor(Color color) {
            this.color=color;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(1, 4, 10, 10);
            g2.dispose();
        }
    }
}