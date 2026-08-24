package com.hospital.view;

import com.hospital.model.User;
import com.hospital.util.SessionManager;
import com.hospital.util.UIUtils;
import com.hospital.view.admin.AdminDashboardPanel;
import com.hospital.view.auth.LoginPanel;
import com.hospital.view.auth.RegisterPanel;
import com.hospital.view.dialog.DatabaseConfigDialog;
import com.hospital.view.patient.PatientDashboardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Application Window managing views and navigation.
 */
public class MainFrame extends JFrame {

    public static final String CARD_LOGIN = "CARD_LOGIN";
    public static final String CARD_REGISTER = "CARD_REGISTER";
    public static final String CARD_PATIENT = "CARD_PATIENT";
    public static final String CARD_ADMIN = "CARD_ADMIN";

    private final CardLayout cardLayout;
    private final JPanel cardsContainer;

    private final JPanel topNavBar;
    private final JLabel userBadgeLabel;
    private final JButton logoutBtn;
    private final JButton dbBtn;

    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final PatientDashboardPanel patientDashboardPanel;
    private final AdminDashboardPanel adminDashboardPanel;

    public MainFrame() {
        super("Hospital Appointment Scheduling System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardsContainer = new JPanel(cardLayout);

        // Top App Bar
        topNavBar = new JPanel(new BorderLayout(16, 0));
        topNavBar.setBackground(UIUtils.CARD_BG);
        topNavBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR),
                new EmptyBorder(12, 24, 12, 24)
        ));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandPanel.setOpaque(false);
        JLabel brandLogo = new JLabel("CarePulse");
        brandLogo.setFont(UIUtils.bold(18));
        brandLogo.setForeground(UIUtils.PRIMARY);

        JLabel brandSub = new JLabel("Hospital Management System");
        brandSub.setFont(UIUtils.regular(13));
        brandSub.setForeground(UIUtils.TEXT_MUTED);

        brandPanel.add(brandLogo);
        brandPanel.add(new JSeparator(JSeparator.VERTICAL));
        brandPanel.add(brandSub);
        topNavBar.add(brandPanel, BorderLayout.WEST);

        JPanel userNavPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userNavPanel.setOpaque(false);

        userBadgeLabel = new JLabel("User: Guest");
        userBadgeLabel.setFont(UIUtils.medium(13));
        userBadgeLabel.setForeground(UIUtils.TEXT_PRIMARY);

        dbBtn = new JButton("DB Settings");
        dbBtn.setFont(UIUtils.regular(12));
        dbBtn.setFocusPainted(false);
        dbBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dbBtn.addActionListener(e -> new DatabaseConfigDialog(this).setVisible(true));

        logoutBtn = UIUtils.createSecondaryButton("Sign Out");
        logoutBtn.setFont(UIUtils.medium(12));
        logoutBtn.addActionListener(e -> performLogout());

        userNavPanel.add(userBadgeLabel);
        userNavPanel.add(dbBtn);
        userNavPanel.add(logoutBtn);
        topNavBar.add(userNavPanel, BorderLayout.EAST);

        // Views
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        patientDashboardPanel = new PatientDashboardPanel(this);
        adminDashboardPanel = new AdminDashboardPanel(this);

        cardsContainer.add(loginPanel, CARD_LOGIN);
        cardsContainer.add(registerPanel, CARD_REGISTER);
        cardsContainer.add(patientDashboardPanel, CARD_PATIENT);
        cardsContainer.add(adminDashboardPanel, CARD_ADMIN);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topNavBar, BorderLayout.NORTH);
        getContentPane().add(cardsContainer, BorderLayout.CENTER);

        showLoginScreen();
    }

    public void showLoginScreen() {
        topNavBar.setVisible(false);
        loginPanel.resetFields();
        cardLayout.show(cardsContainer, CARD_LOGIN);
    }

    public void showRegisterScreen() {
        topNavBar.setVisible(false);
        registerPanel.resetFields();
        cardLayout.show(cardsContainer, CARD_REGISTER);
    }

    public void showPatientDashboard() {
        updateNavbarUser();
        topNavBar.setVisible(true);
        patientDashboardPanel.refreshDashboardData();
        cardLayout.show(cardsContainer, CARD_PATIENT);
    }

    public void showAdminDashboard() {
        updateNavbarUser();
        topNavBar.setVisible(true);
        adminDashboardPanel.refreshDashboardData();
        cardLayout.show(cardsContainer, CARD_ADMIN);
    }

    private void updateNavbarUser() {
        User u = SessionManager.getCurrentUser();
        if (u != null) {
            userBadgeLabel.setText("User: " + u.getName() + " (" + u.getRole().getDisplayName() + ")");
        } else {
            userBadgeLabel.setText("User: Guest");
        }
    }

    private void performLogout() {
        boolean confirm = UIUtils.confirm(this, "Are you sure you want to sign out?", "Confirm Sign Out");
        if (confirm) {
            SessionManager.logout();
            showLoginScreen();
        }
    }
}
