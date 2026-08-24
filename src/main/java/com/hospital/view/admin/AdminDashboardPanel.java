package com.hospital.view.admin;

import com.hospital.model.Admin;
import com.hospital.service.DashboardService;
import com.hospital.util.SessionManager;
import com.hospital.util.UIUtils;
import com.hospital.view.MainFrame;
import com.hospital.view.common.StatCard;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Main Admin Portal Dashboard displaying global hospital metrics, Doctor Management, and Appointment Management tabs.
 */
public class AdminDashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final DashboardService dashboardService;

    private JLabel adminMetaLabel;
    private StatCard patientsCard;
    private StatCard doctorsCard;
    private StatCard totalApptsCard;
    private StatCard pendingCard;

    private JTabbedPane tabbedPane;
    private ManageDoctorsPanel manageDoctorsPanel;
    private ManageAppointmentsPanel manageAppointmentsPanel;

    public AdminDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.dashboardService = new DashboardService();

        initComponents();
        refreshDashboardData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Header & KPI Stats Container
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // 1. Admin Banner
        JPanel banner = new JPanel(new BorderLayout(12, 4));
        banner.setBackground(UIUtils.CARD_BG);
        banner.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel titleLabel = new JLabel("Hospital Administration Control Center");
        titleLabel.setFont(UIUtils.bold(20));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        adminMetaLabel = new JLabel("Administrator • Master Access");
        adminMetaLabel.setFont(UIUtils.regular(13));
        adminMetaLabel.setForeground(UIUtils.TEXT_MUTED);

        banner.add(titleLabel, BorderLayout.NORTH);
        banner.add(adminMetaLabel, BorderLayout.SOUTH);
        topContainer.add(banner);
        topContainer.add(Box.createVerticalStrut(12));

        // 2. KPI Stat Cards Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);

        patientsCard = new StatCard("TOTAL PATIENTS", "0", "Registered users", UIUtils.PRIMARY);
        doctorsCard = new StatCard("TOTAL DOCTORS", "0", "Active medical staff", new Color(14, 165, 233));
        totalApptsCard = new StatCard("ALL APPOINTMENTS", "0", "Total bookings", new Color(139, 92, 246));
        pendingCard = new StatCard("PENDING APPROVAL", "0", "Action required", new Color(245, 158, 11));

        statsRow.add(patientsCard);
        statsRow.add(doctorsCard);
        statsRow.add(totalApptsCard);
        statsRow.add(pendingCard);

        topContainer.add(statsRow);
        add(topContainer, BorderLayout.NORTH);

        // Center Content Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.medium(14));
        tabbedPane.setBackground(UIUtils.BG_MAIN);

        manageAppointmentsPanel = new ManageAppointmentsPanel(this::refreshDashboardData);
        manageDoctorsPanel = new ManageDoctorsPanel(mainFrame, this::refreshDashboardData);

        tabbedPane.addTab("Manage Appointments", manageAppointmentsPanel);
        tabbedPane.addTab("Manage Doctors", manageDoctorsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshDashboardData() {
        Admin admin = SessionManager.getCurrentAdmin();
        if (admin != null) {
            adminMetaLabel.setText("Logged in as: " + admin.getName() + " (" + admin.getEmail() + ")");
        }

        DashboardService.AdminStats stats = dashboardService.getAdminStats();
        patientsCard.setValue(String.valueOf(stats.totalPatients));
        doctorsCard.setValue(String.valueOf(stats.totalDoctors));
        totalApptsCard.setValue(String.valueOf(stats.totalAppointments));
        pendingCard.setValue(String.valueOf(stats.pendingAppointments));

        if (manageAppointmentsPanel != null) {
            manageAppointmentsPanel.refreshAppointments();
        }
        if (manageDoctorsPanel != null) {
            manageDoctorsPanel.refreshDoctors();
        }
    }
}
