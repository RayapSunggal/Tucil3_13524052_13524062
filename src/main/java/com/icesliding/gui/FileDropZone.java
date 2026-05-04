package com.icesliding.gui;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

class FileDropZone extends JPanel {
    enum State {
        EMPTY,
        VALID,
        INVALID
    }

    private State state=State.EMPTY;
    private String title="Pilih file input";
    private String detail="Klik atau drop file .txt";
    private boolean hover;
    private Consumer<File> onFile;

    FileDropZone() {
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(300, 116));
        setMinimumSize(new Dimension(260, 116));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 116));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()){
                    browse();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover=true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover=false;
                repaint();
            }
        });

        try{
            new DropTarget(this, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
                @Override
                public void dragEnter(DropTargetDragEvent event) {
                    hover=true;
                    repaint();
                }

                @Override
                public void dragExit(DropTargetEvent event) {
                    hover=false;
                    repaint();
                }

                @Override
                @SuppressWarnings("unchecked")
                public void drop(DropTargetDropEvent event) {
                    hover=false;
                    try{
                        event.acceptDrop(DnDConstants.ACTION_COPY);
                        List<File> files=(List<File>)
                                event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        if (!files.isEmpty() && onFile!=null){
                            onFile.accept(files.get(0));
                        }
                    }
                    catch (Exception ignored){
                        showInvalid("Drop file gagal");
                    }
                    repaint();
                }
            }, true);
        }
        catch (Exception ignored){
        }
    }

    void setOnFile(Consumer<File> onFile) {
        this.onFile=onFile;
    }

    void showEmpty() {
        state=State.EMPTY;
        title="Pilih file input";
        detail="Klik atau drop file .txt";
        repaint();
    }

    void showValid(String fileName, String summary) {
        state=State.VALID;
        title=shorten(fileName, 30);
        detail=summary;
        repaint();
    }

    void showInvalid(String message) {
        state=State.INVALID;
        title="Input tidak valid";
        detail=shorten(message, 38);
        repaint();
    }

    private void browse() {
        JFileChooser chooser=new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text input (*.txt)", "txt"));
        chooser.setDialogTitle("Pilih file input");
        Window parent=SwingUtilities.getWindowAncestor(this);
        if (chooser.showOpenDialog(parent)==JFileChooser.APPROVE_OPTION && onFile!=null){
            onFile.accept(chooser.getSelectedFile());
        }
    }

    private String shorten(String value, int max) {
        if (value==null || value.length()<=max){
            return value;
        }
        return value.substring(0, Math.max(0, max-3))+"...";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w=getWidth();
        int h=getHeight();
        Color fill=fillColor();
        Color border=borderColor();

        g2.setColor(fill);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 8, 8));

        float[] dash=state==State.EMPTY ? new float[]{9f, 6f} : new float[]{1f, 0f};
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dash, 0));
        g2.setColor(border);
        g2.draw(new RoundRectangle2D.Float(1, 1, w-2, h-2, 8, 8));

        paintGlyph(g2, w);

        g2.setFont(UITheme.SECTION);
        g2.setColor(UITheme.INK);
        drawCentered(g2, title, 54, w);

        g2.setFont(UITheme.BODY);
        g2.setColor(UITheme.INK_MUTED);
        drawCentered(g2, detail, 78, w);

        g2.dispose();
    }

    private Color fillColor() {
        if (state==State.VALID){
            return UITheme.GREEN_SOFT;
        }
        if (state==State.INVALID){
            return UITheme.RED_SOFT;
        }
        return hover ? UITheme.TEAL_SOFT : UITheme.SURFACE_ALT;
    }

    private Color borderColor() {
        if (state==State.VALID){
            return UITheme.GREEN;
        }
        if (state==State.INVALID){
            return UITheme.RED;
        }
        return hover ? UITheme.TEAL : UITheme.BORDER;
    }

    private void paintGlyph(Graphics2D g2, int width) {
        int cx=width/2;
        int cy=28;
        Color color=borderColor();
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);

        if (state==State.VALID){
            g2.drawLine(cx-9, cy, cx-2, cy+7);
            g2.drawLine(cx-2, cy+7, cx+11, cy-8);
        }
        else if (state==State.INVALID){
            g2.drawLine(cx-9, cy-9, cx+9, cy+9);
            g2.drawLine(cx+9, cy-9, cx-9, cy+9);
        }
        else{
            g2.drawLine(cx, cy+11, cx, cy-10);
            g2.drawLine(cx-8, cy-2, cx, cy-10);
            g2.drawLine(cx+8, cy-2, cx, cy-10);
            g2.drawLine(cx-12, cy+12, cx+12, cy+12);
        }
    }

    private void drawCentered(Graphics2D g2, String text, int baselineY, int width) {
        FontMetrics fm=g2.getFontMetrics();
        g2.drawString(text, (width-fm.stringWidth(text))/2, baselineY);
    }
}