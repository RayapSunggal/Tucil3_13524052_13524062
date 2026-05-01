package com.icesliding.gui;

import com.icesliding.model.Board;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

public class BoardPanel extends JPanel {
    private static final int MIN_TILE=28;
    private static final int MAX_TILE=68;
    private static final int PADDING=28;

    private Board board;

    public BoardPanel() {
        setBackground(UITheme.SURFACE);
        setPreferredSize(new Dimension(620, 440));
    }

    public void setBoard(Board board) {
        this.board=board;
        revalidate();
        repaint();
    }

    public void clearBoard() {
        this.board=null;
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        paintCanvas(g2);
        if (board==null){
            paintEmptyState(g2);
        }
        else{
            paintBoard(g2);
        }
        g2.dispose();
    }

    private void paintCanvas(Graphics2D g2) {
        int w=getWidth();
        int h=getHeight();
        g2.setColor(UITheme.SURFACE);
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(0xEAF1F4));
        for (int x=0; x < w; x+=24){
            g2.drawLine(x, 0, x, h);
        }
        for (int y=0; y < h; y+=24){
            g2.drawLine(0, y, w, y);
        }
    }

    private void paintEmptyState(Graphics2D g2) {
        int boxW=Math.min(360, Math.max(240, getWidth() - 80));
        int boxH=138;
        int x=(getWidth() - boxW) / 2;
        int y=(getHeight() - boxH) / 2;

        g2.setColor(UITheme.SURFACE_ALT);
        g2.fill(new RoundRectangle2D.Float(x, y, boxW, boxH, 8, 8));
        g2.setColor(UITheme.BORDER);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, boxW - 1, boxH - 1, 8, 8));

        g2.setFont(UITheme.SECTION);
        g2.setColor(UITheme.INK);
        drawCentered(g2, "Preview papan kosong", x, y + 44, boxW);
        g2.setFont(UITheme.BODY);
        g2.setColor(UITheme.INK_MUTED);
        drawCentered(g2, "Muat file .txt untuk melihat bentuk input.", x, y + 72, boxW);
        drawCentered(g2, "Validasi akan muncul setelah file dimuat.", x, y + 94, boxW);
    }

    private void paintBoard(Graphics2D g2) {
        int availableW=Math.max(1, getWidth() - (PADDING * 2));
        int availableH=Math.max(1, getHeight() - (PADDING * 2));
        int tile=Math.max(MIN_TILE, Math.min(MAX_TILE,
                Math.min(availableW / board.cols, availableH / board.rows)));

        int boardW=tile * board.cols;
        int boardH=tile * board.rows;
        int startX=(getWidth() - boardW) / 2;
        int startY=(getHeight() - boardH) / 2;

        g2.setColor(new Color(0, 0, 0, 18));
        g2.fill(new RoundRectangle2D.Float(startX + 8, startY + 10, boardW, boardH, 12, 12));

        for (int r=0; r < board.rows; r++){
            for (int c=0; c < board.cols; c++){
                int x=startX + c * tile;
                int y=startY + r * tile;
                paintTile(g2, r, c, x, y, tile);
            }
        }
    }

    private void paintTile(Graphics2D g2, int row, int col, int x, int y, int tile) {
        char value=board.grid[row][col];
        TileStyle style=styleFor(value);
        int arc=Math.max(7, tile / 5);

        g2.setColor(style.background);
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 2, tile - 4, tile - 4, arc, arc));
        g2.setColor(style.border);
        g2.setStroke(new BasicStroke(value=='Z' || value=='O' ? 2f : 1f));
        g2.draw(new RoundRectangle2D.Float(x + 2.5f, y + 2.5f, tile - 5, tile - 5, arc, arc));

        if (style.label!=null){
            g2.setFont(tile>=44 ? UITheme.SECTION.deriveFont(15f) : UITheme.BODY_BOLD);
            g2.setColor(style.foreground);
            drawCentered(g2, style.label, x, y + (tile + g2.getFontMetrics().getAscent()) / 2 - 2, tile);
        }

        if (tile>=46){
            String cost=String.valueOf(board.cost[row][col]);
            g2.setFont(UITheme.SMALL);
            g2.setColor(value=='X' ? new Color(0xB8C4CA) : UITheme.INK_MUTED);
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(cost, x + tile - fm.stringWidth(cost) - 7, y + tile - 7);
        }
    }

    private TileStyle styleFor(char value) {
        if (value=='X'){
            return new TileStyle(UITheme.STONE, new Color(0x21313A), new Color(0xF2F5F6), "X");
        }
        if (value=='L'){
            return new TileStyle(UITheme.RED_SOFT, UITheme.RED, UITheme.RED, "L");
        }
        if (value=='O'){
            return new TileStyle(UITheme.GREEN_SOFT, UITheme.GREEN, UITheme.GREEN, "O");
        }
        if (value=='Z'){
            return new TileStyle(UITheme.TEAL_SOFT, UITheme.TEAL, UITheme.TEAL, "Z");
        }
        if (value>='0' && value<='9'){
            return new TileStyle(UITheme.AMBER_SOFT, UITheme.AMBER, new Color(0xA15F00), String.valueOf(value));
        }
        return new TileStyle(UITheme.ICE, new Color(0xB7D4DE), UITheme.INK_MUTED, null);
    }

    private void drawCentered(Graphics2D g2, String text, int x, int baselineY, int width) {
        FontMetrics fm=g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, baselineY);
    }

    private static final class TileStyle {
        final Color background;
        final Color border;
        final Color foreground;
        final String label;

        TileStyle(Color background, Color border, Color foreground, String label) {
            this.background=background;
            this.border=border;
            this.foreground=foreground;
            this.label=label;
        }
    }
}