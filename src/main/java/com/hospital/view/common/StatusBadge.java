package com.hospital.view.common;

import com.hospital.model.AppointmentStatus;
import com.hospital.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer displaying modern pill badges for appointment statuses.
 */
public class StatusBadge extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String text = (value != null) ? value.toString() : "";
        AppointmentStatus status = AppointmentStatus.fromString(text);

        PillBadgePanel badge = new PillBadgePanel(status.getDisplayName(), status);
        if (isSelected) {
            badge.setBackground(new Color(241, 245, 249));
        } else {
            badge.setBackground(table.getBackground());
        }
        return badge;
    }

    public static class PillBadgePanel extends JPanel {
        private final String text;
        private final AppointmentStatus status;

        public PillBadgePanel(String text, AppointmentStatus status) {
            this.text = text;
            this.status = status;
            setLayout(new GridBagLayout());
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bgColor;
            Color textColor;

            switch (status) {
                case CONFIRMED:
                    bgColor = UIUtils.STATUS_CONFIRMED_BG;
                    textColor = UIUtils.STATUS_CONFIRMED_TEXT;
                    break;
                case CANCELLED:
                    bgColor = UIUtils.STATUS_CANCELLED_BG;
                    textColor = UIUtils.STATUS_CANCELLED_TEXT;
                    break;
                case PENDING:
                default:
                    bgColor = UIUtils.STATUS_PENDING_BG;
                    textColor = UIUtils.STATUS_PENDING_TEXT;
                    break;
            }

            int w = 96;
            int h = 24;
            int x = (getWidth() - w) / 2;
            int y = (getHeight() - h) / 2;

            g2.setColor(bgColor);
            g2.fillRoundRect(x, y, w, h, 14, 14);

            g2.setColor(textColor);
            g2.setFont(UIUtils.bold(11));
            FontMetrics fm = g2.getFontMetrics();
            int strX = x + (w - fm.stringWidth(text)) / 2;
            int strY = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, strX, strY);

            g2.dispose();
        }
    }
}
