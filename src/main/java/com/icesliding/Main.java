package com.icesliding;

import com.icesliding.gui.MainWindow;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try{
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ScrollBar.thumbArc", 8);
            UIManager.put("ScrollBar.trackArc", 8);
        }
        catch (Exception ignored){
            try{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ignoredAgain){
            }
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow window=new MainWindow();
            window.setVisible(true);
        });
    }
}