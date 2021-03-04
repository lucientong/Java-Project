package com.tyz.util;

import javax.swing.*;

public class ViewTool {
    public static void showWarning(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showTip(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static int getUserChoice(JFrame parent, String title, String message, int type) {
        return JOptionPane.showConfirmDialog(parent, message, title, type);
    }
}
