package com.hospital.view.patient;

import com.hospital.model.Patient;
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
 * Main Patient Portal Dashboard containing KPI metrics and sub-tabs for Doctor Directory and My Appointments.
 */
public class PatientDashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final DashboardService dashboardService;

    private JLabel welcomeLabel;
    private JLabel patientMetaLabel;
    private StatCard totalCard;
    private StatCard confirmedCard;
    private StatCard pendingCard;
    private StatCard cancelledCard;

    private JTabbedPane tabbedPane;
    private DoctorListPanel doctorListPanel;
    private MyAppointmentsPanel myAppointmentsPanel;

    public PatientDashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.dashboardService = new DashboardService();

        initComponents();
        refreshDashboardData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        // Top Greeting Banner + KPI Stat Cards Panel
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        // 1. Welcome Banner
        JPanel banner = new JPanel(new BorderLayout(12, 4));
        banner.setBackground(UIUtils.CARD_BG);
        banner.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(UIUtils.bold(20));
        welcomeLabel.setForeground(UIUtils.TEXT_PRIMARY);

        patientMetaLabel = new JLabel("Patient Details");
        patientMetaLabel.setFont(UIUtils.regular(13));
        patientMetaLabel.setForeground(UIUtils.TEXT_MUTED);

        banner.add(welcomeLabel, BorderLayout.NORTH);
        banner.add(patientMetaLabel, BorderLayout.SOUTH);
        topContainer.add(banner);
        topContainer.add(Box.createVerticalStrut(12));

        // 2. KPI Stat Cards Row
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 12, 0));
        statsRow.setOpaque(false);

        totalCard = new StatCard("TOTAL BOOKINGS", "0", "All time appointments", UIUtils.PRIMARY);
        confirmedCard = new StatCard("CONFIRMED", "0", "Scheduled visits", new Color(16, 185, 129));
        pendingCard = new StatCard("PENDING APPROVAL", "0", "Awaiting confirmation", new Color(245, 158, 11));
        cancelledCard = new StatCard("CANCELLED", "0", "Slots released", new Color(239, 68, 68));

        statsRow.add(totalCard);
        statsRow.add(confirmedCard);
        statsRow.add(pendingCard);
        statsRow.add(cancelledCard);

        topContainer.add(statsRow);
        add(topContainer, BorderLayout.NORTH);

        // Center Content Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.medium(14));
        tabbedPane.setBackground(UIUtils.BG_MAIN);

        doctorListPanel = new DoctorListPanel(mainFrame, this::onAppointmentBooked);
        myAppointmentsPanel = new MyAppointmentsPanel(this::refreshDashboardData);

        tabbedPane.addTab("Find Doctors & Book", doctorListPanel);
        tabbedPane.addTab("My Appointments", myAppointmentsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshDashboardData() {
        Patient patient = SessionManager.getCurrentPatient();
        if (patient == null) return;

        welcomeLabel.setText("Welcome back, " + patient.getName() + "!");
        patientMetaLabel.setText("Patient ID #" + patient.getId() + "  |  Email: " + patient.getEmail() + "  |  Phone: " + patient.getPhone());

        DashboardService.PatientStats stats = dashboardService.getPatientStats(patient.getId());
        totalCard.setValue(String.valueOf(stats.totalBookings));
        confirmedCard.setValue(String.valueOf(stats.confirmedBookings));
        pendingCard.setValue(String.valueOf(stats.pendingBookings));
        cancelledCard.setValue(String.valueOf(stats.cancelledBookings));

        if (myAppointmentsPanel != null) {
            myAppointmentsPanel.refreshAppointments();
        }
        if (doctorListPanel != null) {
            doctorListPanel.refreshDoctors();
        }
    }

    private void onAppointmentBooked() {
        refreshDashboardData();
        tabbedPane.setSelectedComponent(myAppointmentsPanel);
    }
}
