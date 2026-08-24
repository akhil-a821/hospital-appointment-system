package com.hospital.view.dialog;

import com.hospital.util.DBConnection;
import com.hospital.util.DatabaseInitializer;
import com.hospital.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for configuring MySQL database connection settings.
 */
public class DatabaseConfigDialog extends JDialog {

    private JTextField hostField;
    private JTextField portField;
    private JTextField dbNameField;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public DatabaseConfigDialog(JFrame parent) {
        super(parent, "Database Configuration & Setup", true);
        initComponents();
        loadCurrentConfig();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(UIUtils.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(8, 4));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("MySQL Database Connection");
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel descLabel = new JLabel("Configure your MySQL server connection details below.");
        descLabel.setFont(UIUtils.regular(13));
        descLabel.setForeground(UIUtils.TEXT_MUTED);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        hostField = UIUtils.createTextField(20);
        portField = UIUtils.createTextField(20);
        dbNameField = UIUtils.createTextField(20);
        userField = UIUtils.createTextField(20);
        passField = UIUtils.createPasswordField(20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Host:", hostField);
        addFormRow(formPanel, gbc, row++, "Port:", portField);
        addFormRow(formPanel, gbc, row++, "Database:", dbNameField);
        addFormRow(formPanel, gbc, row++, "Username:", userField);
        addFormRow(formPanel, gbc, row++, "Password:", passField);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIUtils.medium(12));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton testBtn = UIUtils.createSecondaryButton("Test Connection");
        JButton initBtn = UIUtils.createSecondaryButton("Initialize Schema");
        JButton saveBtn = UIUtils.createPrimaryButton("Save & Connect");
        JButton closeBtn = UIUtils.createSecondaryButton("Close");

        testBtn.addActionListener(e -> onTestConnection());
        initBtn.addActionListener(e -> onInitSchema());
        saveBtn.addActionListener(e -> onSaveAndConnect());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(testBtn);
        buttonPanel.add(initBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.medium(13));
        lbl.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void loadCurrentConfig() {
        hostField.setText(DBConnection.getHost());
        portField.setText(String.valueOf(DBConnection.getPort()));
        dbNameField.setText(DBConnection.getDbName());
        userField.setText(DBConnection.getUser());
        passField.setText(DBConnection.getPassword());
    }

    private void onTestConnection() {
        try {
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String db = dbNameField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            boolean success = DBConnection.testConnection(host, port, "", user, pass);
            if (success) {
                statusLabel.setForeground(new Color(16, 185, 129));
                statusLabel.setText("MySQL server connected successfully!");
            } else {
                statusLabel.setForeground(new Color(239, 68, 68));
                statusLabel.setText("Connection failed! Check host, port, and credentials.");
            }
        } catch (Exception ex) {
            statusLabel.setForeground(new Color(239, 68, 68));
            statusLabel.setText("Invalid port or settings.");
        }
    }

    private void onInitSchema() {
        onSaveConfig();
        boolean success = DatabaseInitializer.initializeDatabase();
        if (success) {
            statusLabel.setForeground(new Color(16, 185, 129));
            statusLabel.setText("Database & tables initialized successfully!");
            UIUtils.showSuccess(this, "Database schema and seed data loaded successfully!");
        } else {
            statusLabel.setForeground(new Color(239, 68, 68));
            statusLabel.setText("Failed to initialize database schema.");
            UIUtils.showError(this, "Could not initialize database. Ensure MySQL server is running.");
        }
    }

    private void onSaveAndConnect() {
        onSaveConfig();
        dispose();
    }

    private void onSaveConfig() {
        try {
            String host = hostField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String db = dbNameField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            DBConnection.saveProperties(host, port, db, user, pass);
            statusLabel.setForeground(new Color(16, 185, 129));
            statusLabel.setText("Settings saved.");
        } catch (Exception ex) {
            statusLabel.setForeground(new Color(239, 68, 68));
            statusLabel.setText("Error saving settings.");
        }
    }
}
