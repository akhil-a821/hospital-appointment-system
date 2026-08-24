package com.hospital.view.common;

import com.hospital.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Modern styled JTable with alternating row colors, sleek headers, and padded cells.
 */
public class CustomTable extends JTable {

    public CustomTable(TableModel model) {
        super(model);
        initStyle();
    }

    public CustomTable() {
        super();
        initStyle();
    }

    private void initStyle() {
        setRowHeight(42);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(UIUtils.regular(13));
        setForeground(UIUtils.TEXT_PRIMARY);
        setSelectionBackground(new Color(235, 245, 255));
        setSelectionForeground(UIUtils.TEXT_PRIMARY);
        setFillsViewportHeight(true);

        // Header Styling
        JTableHeader header = getTableHeader();
        header.setFont(UIUtils.bold(13));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(new Color(71, 85, 105));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        // Default cell renderer with padding and alternating row colors
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 250, 252));
                    }
                    c.setForeground(UIUtils.TEXT_PRIMARY);
                }
                return c;
            }
        });
    }
}
