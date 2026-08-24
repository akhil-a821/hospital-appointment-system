package com.hospital.view.auth;

import com.hospital.service.AuthService;
import com.hospital.util.UIUtils;
import com.hospital.view.MainFrame;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Registration panel for new patients.
 */
public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JComboBox<String> genderCombo;
    private JTextField ageField;
    private JLabel errorLabel;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BG_MAIN);

        JPanel card = new JPanel(new BorderLayout(16, 16));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(28, 36, 28, 36)
        ));
        card.setPreferredSize(new Dimension(500, 620));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Patient Registration", SwingConstants.CENTER);
        titleLabel.setFont(UIUtils.bold(20));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Create your personal patient portal account", SwingConstants.CENTER);
        subtitleLabel.setFont(UIUtils.regular(13));
        subtitleLabel.setForeground(UIUtils.TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        card.add(headerPanel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = UIUtils.createTextField(20);
        emailField = UIUtils.createTextField(20);
        passwordField = UIUtils.createPasswordField(20);
        phoneField = UIUtils.createTextField(20);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(UIUtils.regular(13));
        genderCombo.setBackground(Color.WHITE);
        ageField = UIUtils.createTextField(20);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Full Name *", nameField);
        addFormRow(formPanel, gbc, row++, "Email Address *", emailField);
        addFormRow(formPanel, gbc, row++, "Password *", passwordField);
        addFormRow(formPanel, gbc, row++, "Phone Number *", phoneField);
        addFormRow(formPanel, gbc, row++, "Gender", genderCombo);
        addFormRow(formPanel, gbc, row++, "Age", ageField);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtils.medium(12));
        errorLabel.setForeground(new Color(239, 68, 68));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(errorLabel, gbc);

        card.add(formPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        JButton registerBtn = UIUtils.createPrimaryButton("Create Account");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerBtn.addActionListener(e -> performRegister());
        footerPanel.add(registerBtn);
        footerPanel.add(Box.createVerticalStrut(10));

        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        loginLinkPanel.setOpaque(false);
        JLabel hasAccLabel = new JLabel("Already have an account?");
        hasAccLabel.setFont(UIUtils.regular(13));
        hasAccLabel.setForeground(UIUtils.TEXT_SECONDARY);

        JButton loginLinkBtn = new JButton("Sign In");
        loginLinkBtn.setFont(UIUtils.bold(13));
        loginLinkBtn.setForeground(UIUtils.PRIMARY);
        loginLinkBtn.setBorderPainted(false);
        loginLinkBtn.setContentAreaFilled(false);
        loginLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLinkBtn.addActionListener(e -> mainFrame.showLoginScreen());

        loginLinkPanel.add(hasAccLabel);
        loginLinkPanel.add(loginLinkBtn);
        footerPanel.add(loginLinkPanel);

        card.add(footerPanel, BorderLayout.SOUTH);

        add(card);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UIUtils.medium(13));
        lbl.setForeground(UIUtils.TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(field, gbc);
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();
        String ageStr = ageField.getText().trim();

        String error = authService.registerPatient(name, email, password, phone, gender, ageStr);
        if (error == null) {
            UIUtils.showSuccess(this, "Account created successfully! Please sign in with your credentials.");
            resetFields();
            mainFrame.showLoginScreen();
        } else {
            errorLabel.setText(error);
        }
    }

    public void resetFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        phoneField.setText("");
        genderCombo.setSelectedIndex(0);
        ageField.setText("");
        errorLabel.setText(" ");
    }
}
