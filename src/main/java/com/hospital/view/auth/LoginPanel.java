package com.hospital.view.auth;

import com.hospital.model.Role;
import com.hospital.service.AuthService;
import com.hospital.util.UIUtils;
import com.hospital.view.MainFrame;
import com.hospital.view.dialog.DatabaseConfigDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modern Login Screen with Role Selector and 1-Click Demo Access for both Patient and Admin.
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService;

    private JTextField emailField;
    private JPasswordField passwordField;
    private JRadioButton patientRadio;
    private JRadioButton adminRadio;
    private JLabel errorLabel;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(UIUtils.BG_MAIN);

        // Center Login Card
        JPanel card = new JPanel(new BorderLayout(14, 14));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(28, 36, 28, 36)
        ));
        card.setPreferredSize(new Dimension(480, 620));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        headerPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("CarePulse Medical Center", SwingConstants.CENTER);
        logoLabel.setFont(UIUtils.bold(22));
        logoLabel.setForeground(UIUtils.PRIMARY);

        JLabel titleLabel = new JLabel("Portal Sign In", SwingConstants.CENTER);
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Sign in to access your healthcare portal", SwingConstants.CENTER);
        subtitleLabel.setFont(UIUtils.regular(13));
        subtitleLabel.setForeground(UIUtils.TEXT_MUTED);

        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        card.add(headerPanel, BorderLayout.NORTH);

        // Form Body
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        // Role Selector Radio
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        rolePanel.setOpaque(false);
        patientRadio = new JRadioButton("Patient Portal", true);
        patientRadio.setFont(UIUtils.medium(13));
        patientRadio.setForeground(UIUtils.TEXT_PRIMARY);
        patientRadio.setOpaque(false);

        adminRadio = new JRadioButton("Admin Portal", false);
        adminRadio.setFont(UIUtils.medium(13));
        adminRadio.setForeground(UIUtils.TEXT_PRIMARY);
        adminRadio.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(patientRadio);
        group.add(adminRadio);
        rolePanel.add(patientRadio);
        rolePanel.add(adminRadio);

        bodyPanel.add(rolePanel);
        bodyPanel.add(Box.createVerticalStrut(6));

        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(UIUtils.medium(13));
        emailLabel.setForeground(UIUtils.TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(emailLabel);
        bodyPanel.add(Box.createVerticalStrut(4));

        emailField = UIUtils.createTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(emailField);
        bodyPanel.add(Box.createVerticalStrut(10));

        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIUtils.medium(13));
        passLabel.setForeground(UIUtils.TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(passLabel);
        bodyPanel.add(Box.createVerticalStrut(4));

        passwordField = UIUtils.createPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(passwordField);
        bodyPanel.add(Box.createVerticalStrut(6));

        // Error message
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtils.medium(12));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyPanel.add(errorLabel);
        bodyPanel.add(Box.createVerticalStrut(6));

        // Sign In Button
        JButton signInBtn = UIUtils.createPrimaryButton("Sign In");
        signInBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        signInBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        signInBtn.addActionListener(e -> performLogin());
        bodyPanel.add(signInBtn);
        bodyPanel.add(Box.createVerticalStrut(8));

        // Register Link
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerLinkPanel.setOpaque(false);
        registerLinkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerLinkPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel noAccLabel = new JLabel("Don't have an account?");
        noAccLabel.setFont(UIUtils.regular(12));
        noAccLabel.setForeground(UIUtils.TEXT_SECONDARY);

        JButton registerLinkBtn = new JButton("Register as Patient");
        registerLinkBtn.setFont(UIUtils.bold(12));
        registerLinkBtn.setForeground(UIUtils.PRIMARY);
        registerLinkBtn.setBorderPainted(false);
        registerLinkBtn.setContentAreaFilled(false);
        registerLinkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLinkBtn.addActionListener(e -> mainFrame.showRegisterScreen());

        registerLinkPanel.add(noAccLabel);
        registerLinkPanel.add(registerLinkBtn);
        bodyPanel.add(registerLinkPanel);

        bodyPanel.add(Box.createVerticalStrut(10));

        // Dedicated 1-Click Demo Access Box (Both Patient & Admin)
        JPanel demoBox = new JPanel(new GridLayout(2, 1, 6, 6));
        demoBox.setBackground(UIUtils.PRIMARY_LIGHT);
        demoBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(186, 230, 253), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        demoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        demoBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton demoPatientBtn = new JButton("1-Click Demo Login as PATIENT (patient@hospital.com)");
        demoPatientBtn.setFont(UIUtils.bold(12));
        demoPatientBtn.setForeground(new Color(13, 110, 253));
        demoPatientBtn.setBackground(Color.WHITE);
        demoPatientBtn.setFocusPainted(false);
        demoPatientBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        demoPatientBtn.addActionListener(e -> {
            patientRadio.setSelected(true);
            emailField.setText("patient@hospital.com");
            passwordField.setText("patient123");
            performLogin();
        });

        JButton demoAdminBtn = new JButton("1-Click Demo Login as ADMIN (admin@hospital.com)");
        demoAdminBtn.setFont(UIUtils.bold(12));
        demoAdminBtn.setForeground(new Color(15, 23, 42));
        demoAdminBtn.setBackground(new Color(241, 245, 249));
        demoAdminBtn.setFocusPainted(false);
        demoAdminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        demoAdminBtn.addActionListener(e -> {
            adminRadio.setSelected(true);
            emailField.setText("admin@hospital.com");
            passwordField.setText("admin123");
            performLogin();
        });

        demoBox.add(demoPatientBtn);
        demoBox.add(demoAdminBtn);
        bodyPanel.add(demoBox);

        card.add(bodyPanel, BorderLayout.CENTER);

        // Footer with DB Setup link
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        JButton dbConfigBtn = new JButton("Database Connection Settings");
        dbConfigBtn.setFont(UIUtils.regular(11));
        dbConfigBtn.setForeground(UIUtils.TEXT_MUTED);
        dbConfigBtn.setBorderPainted(false);
        dbConfigBtn.setContentAreaFilled(false);
        dbConfigBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dbConfigBtn.addActionListener(e -> new DatabaseConfigDialog(mainFrame).setVisible(true));
        footerPanel.add(dbConfigBtn);

        card.add(footerPanel, BorderLayout.SOUTH);

        passwordField.addActionListener(e -> performLogin());
        emailField.addActionListener(e -> performLogin());

        add(card);
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role expectedRole = adminRadio.isSelected() ? Role.ADMIN : Role.PATIENT;

        errorLabel.setText("Authenticating...");
        errorLabel.setForeground(UIUtils.PRIMARY);

        SwingUtilities.invokeLater(() -> {
            String error = authService.login(email, password, expectedRole);
            if (error == null) {
                errorLabel.setText(" ");
                passwordField.setText("");
                if (expectedRole == Role.ADMIN) {
                    mainFrame.showAdminDashboard();
                } else {
                    mainFrame.showPatientDashboard();
                }
            } else {
                errorLabel.setForeground(new Color(239, 68, 68));
                errorLabel.setText(error);
            }
        });
    }

    public void resetFields() {
        emailField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
        patientRadio.setSelected(true);
    }
}
