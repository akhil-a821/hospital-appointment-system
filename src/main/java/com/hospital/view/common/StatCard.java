package com.hospital.view.common;

import com.hospital.util.UIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modern KPI Stat Card component displaying title, metric number, and accent indicator.
 */
public class StatCard extends JPanel {

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;
    private final Color accentColor;

    public StatCard(String title, String value, String subtitle, Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.CARD_BG);
        setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        setPreferredSize(new Dimension(200, 105));

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        contentPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(UIUtils.regular(12));
        titleLabel.setForeground(UIUtils.TEXT_MUTED);

        valueLabel = new JLabel(value);
        valueLabel.setFont(UIUtils.bold(26));
        valueLabel.setForeground(UIUtils.TEXT_PRIMARY);

        subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(UIUtils.regular(11));
        subtitleLabel.setForeground(accentColor);

        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);
        contentPanel.add(subtitleLabel);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(accentColor);
        // Draw left accent pill bar
        g2.fillRoundRect(0, 8, 4, getHeight() - 16, 4, 4);
        g2.dispose();
    }
}
