package com.icesliding.gui;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

final class UITheme {
    private UITheme() {
    }

    static final Color APP_BG=new Color(0xF4F7F8);
    static final Color SURFACE=new Color(0xFFFFFF);
    static final Color SURFACE_ALT=new Color(0xEEF4F6);
    static final Color INK=new Color(0x102028);
    static final Color INK_MUTED=new Color(0x607079);
    static final Color INK_FAINT=new Color(0x8A989F);
    static final Color BORDER=new Color(0xD8E2E6);

    static final Color NAVY=new Color(0x17324D);
    static final Color TEAL=new Color(0x159A9C);
    static final Color TEAL_SOFT=new Color(0xDFF5F4);
    static final Color AMBER=new Color(0xE8A137);
    static final Color AMBER_SOFT=new Color(0xFFF2D8);
    static final Color RED=new Color(0xD95550);
    static final Color RED_SOFT=new Color(0xFCE8E6);
    static final Color GREEN=new Color(0x2F9E70);
    static final Color GREEN_SOFT=new Color(0xE3F6EC);
    static final Color ICE=new Color(0xDDEFF5);
    static final Color STONE=new Color(0x344955);

    static final Font TITLE=new Font("Segoe UI", Font.BOLD, 26);
    static final Font SUBTITLE=new Font("Segoe UI", Font.PLAIN, 13);
    static final Font SECTION=new Font("Segoe UI", Font.BOLD, 13);
    static final Font BODY=new Font("Segoe UI", Font.PLAIN, 12);
    static final Font BODY_BOLD=new Font("Segoe UI", Font.BOLD, 12);
    static final Font SMALL=new Font("Segoe UI", Font.PLAIN, 11);
    static final Font MONO=new Font("Consolas", Font.PLAIN, 11);

    static Border lineBorder() {
        return BorderFactory.createLineBorder(BORDER, 1);
    }
}