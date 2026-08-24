package com.hospital.view.patient;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.util.DateUtils;
import com.hospital.util.SessionManager;
import com.hospital.util.UIUtils;
import com.hospital.view.common.CustomTable;
import com.hospital.view.common.StatusBadge;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel displaying the patient's booked appointments with status filtering and cancellation controls.
 */
public class MyAppointmentsPanel extends JPanel {

    private final AppointmentService appointmentService;
    private final Runnable onDataChangedCallback;

    private CustomTable appointmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilterCombo;
    private JLabel countLabel;
    private List<Appointment> currentAppointments;

    public MyAppointmentsPanel(Runnable onDataChangedCallback) {
        this.appointmentService = new AppointmentService();
        this.onDataChangedCallback = onDataChangedCallback;

        initComponents();
        refreshAppointments();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UIUtils.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout(12, 12));
        topBar.setBackground(UIUtils.CARD_BG);
        topBar.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        countLabel = new JLabel("(0 appointments)");
        countLabel.setFont(UIUtils.regular(13));
        countLabel.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(countLabel);
        topBar.add(titlePanel, BorderLayout.WEST);

        // Filter & Action buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(UIUtils.medium(13));
        filterLabel.setForeground(UIUtils.TEXT_PRIMARY);

        statusFilterCombo = new JComboBox<>(new String[]{"All", "Pending", "Confirmed", "Cancelled"});
        statusFilterCombo.setFont(UIUtils.regular(13));
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.setPreferredSize(new Dimension(140, 34));
        statusFilterCombo.addActionListener(e -> applyFilter());

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAppointments());

        JButton cancelBtn = UIUtils.createDangerButton("Cancel Appointment");
        cancelBtn.addActionListener(e -> onCancelSelectedAppointment());

        actionsPanel.add(filterLabel);
        actionsPanel.add(statusFilterCombo);
        actionsPanel.add(refreshBtn);
        actionsPanel.add(cancelBtn);

        topBar.add(actionsPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Appointments Table
        String[] columnNames = {"ID", "Doctor", "Department", "Date", "Time", "Reason for Visit", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new CustomTable(tableModel);
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(220);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(110);

        appointmentTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadge());

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(new LineBorder(UIUtils.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(UIUtils.CARD_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshAppointments() {
        Patient patient = SessionManager.getCurrentPatient();
        if (patient == null) return;

        currentAppointments = appointmentService.getPatientAppointments(patient.getId());
        applyFilter();
    }

    private void applyFilter() {
        if (currentAppointments == null) return;

        String filter = (String) statusFilterCombo.getSelectedItem();
        tableModel.setRowCount(0);

        int count = 0;
        for (Appointment appt : currentAppointments) {
            if (filter == null || filter.equalsIgnoreCase("All") || appt.getStatus().name().equalsIgnoreCase(filter)) {
                tableModel.addRow(new Object[]{
                        "#" + appt.getId(),
                        appt.getDoctorName() != null ? appt.getDoctorName() : "Doctor ID: " + appt.getDoctorId(),
                        appt.getDepartment(),
                        DateUtils.formatDate(appt.getAppointmentDate()),
                        appt.getAppointmentTime(),
                        appt.getReason(),
                        appt.getStatus().getDisplayName()
                });
                count++;
            }
        }

        countLabel.setText("(" + count + " " + (count == 1 ? "appointment" : "appointments") + ")");
    }

    private void onCancelSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showWarning(this, "Please select an appointment from the table to cancel.");
            return;
        }

        String idStr = tableModel.getValueAt(selectedRow, 0).toString().replace("#", "").trim();
        int apptId = Integer.parseInt(idStr);

        String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();
        if (currentStatus.equalsIgnoreCase(AppointmentStatus.CANCELLED.getDisplayName())) {
            UIUtils.showWarning(this, "This appointment is already cancelled.");
            return;
        }

        boolean confirm = UIUtils.confirm(
                this,
                "Are you sure you want to cancel Appointment #" + apptId + "?\nThis time slot will become available again for other patients.",
                "Confirm Appointment Cancellation"
        );

        if (confirm) {
            String err = appointmentService.cancelAppointment(apptId);
            if (err == null) {
                UIUtils.showSuccess(this, "Appointment #" + apptId + " cancelled successfully. The slot is now free.");
                refreshAppointments();
                if (onDataChangedCallback != null) {
                    onDataChangedCallback.run();
                }
            } else {
                UIUtils.showError(this, err);
            }
        }
    }
}
