package com.hospital.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern UI styling utilities, design tokens, fonts, and component factories.
 */
public class UIUtils {

    // Modern Medical Color Palette
    public static final Color PRIMARY = new Color(13, 110, 253);       // #0D6EFD Medical Blue
    public static final Color PRIMARY_HOVER = new Color(11, 94, 215);
    public static final Color PRIMARY_LIGHT = new Color(235, 245, 255);
    public static final Color ACCENT = new Color(14, 165, 233);        // #0EA5E9 Sky Cyan
    public static final Color BG_MAIN = new Color(248, 250, 252);      // #F8FAFC Clean Canvas
    public static final Color BG_SIDEBAR = new Color(15, 23, 42);      // #0F172A Deep Slate
    public static final Color BG_SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color BG_SIDEBAR_ACTIVE = new Color(13, 110, 253);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);
    public static final Color BORDER_COLOR = new Color(226, 232, 240);

    // Status Colors
    public static final Color STATUS_CONFIRMED_BG = new Color(209, 250, 229);
    public static final Color STATUS_CONFIRMED_TEXT = new Color(6, 95, 70);
    public static final Color STATUS_PENDING_BG = new Color(254, 243, 199);
    public static final Color STATUS_PENDING_TEXT = new Color(146, 64, 14);
    public static final Color STATUS_CANCELLED_BG = new Color(254, 226, 226);
    public static final Color STATUS_CANCELLED_TEXT = new Color(153, 27, 27);

    // Typography
    public static final String FONT_FAMILY = "Segoe UI";

    public static Font font(float size, int style) {
        return new Font(FONT_FAMILY, style, (int) size);
    }

    public static Font regular(float size) {
        return font(size, Font.PLAIN);
    }

    public static Font medium(float size) {
        return font(size, Font.BOLD);
    }

    public static Font bold(float size) {
        return font(size, Font.BOLD);
    }

    // Modern Button Factory
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(bold(14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(medium(14));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(new Color(241, 245, 249));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(9, 19, 9, 19)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(226, 232, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(241, 245, 249));
            }
        });
        return btn;
    }

    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(bold(13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(16, 185, 129));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(bold(13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(239, 68, 68));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // Modern Text Field Factory
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(regular(14));
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(PRIMARY);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(regular(14));
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(PRIMARY);
        pf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    // Card Panel with Rounded Border & Subtle Header
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        );
    }

    // Dialog & Notification helpers
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
