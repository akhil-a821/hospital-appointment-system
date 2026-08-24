package com.hospital.view.admin;

import com.hospital.model.Appointment;
import com.hospital.service.AppointmentService;
import com.hospital.util.DateUtils;
import com.hospital.util.UIUtils;
import com.hospital.view.common.CustomTable;
import com.hospital.view.common.StatusBadge;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin Panel for Managing Hospital Appointments (Search, Filter, Confirm, Cancel).
 */
public class ManageAppointmentsPanel extends JPanel {

    private final AppointmentService appointmentService;
    private final Runnable onDataChangedCallback;

    private CustomTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JLabel countLabel;
    private List<Appointment> currentAppointments;

    public ManageAppointmentsPanel(Runnable onDataChangedCallback) {
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
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        countLabel = new JLabel("(0 appointments)");
        countLabel.setFont(UIUtils.regular(13));
        countLabel.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(countLabel);
        topBar.add(titlePanel, BorderLayout.WEST);

        // Search, Filters & Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(UIUtils.medium(13));
        searchField = UIUtils.createTextField(12);
        searchField.putClientProperty("JTextField.placeholderText", "Search patient/doctor/reason...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshAppointments(); }
            public void removeUpdate(DocumentEvent e) { refreshAppointments(); }
            public void changedUpdate(DocumentEvent e) { refreshAppointments(); }
        });

        JLabel filterLabel = new JLabel("Status:");
        filterLabel.setFont(UIUtils.medium(13));
        filterLabel.setForeground(UIUtils.TEXT_PRIMARY);

        statusFilterCombo = new JComboBox<>(new String[]{"All", "Pending", "Confirmed", "Cancelled"});
        statusFilterCombo.setFont(UIUtils.regular(13));
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.setPreferredSize(new Dimension(130, 34));
        statusFilterCombo.addActionListener(e -> refreshAppointments());

        JButton confirmBtn = UIUtils.createSuccessButton("Confirm");
        confirmBtn.addActionListener(e -> onConfirmAppointment());

        JButton cancelBtn = UIUtils.createDangerButton("Cancel");
        cancelBtn.addActionListener(e -> onCancelAppointment());

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAppointments());

        actionsPanel.add(searchIcon);
        actionsPanel.add(searchField);
        actionsPanel.add(filterLabel);
        actionsPanel.add(statusFilterCombo);
        actionsPanel.add(confirmBtn);
        actionsPanel.add(cancelBtn);
        actionsPanel.add(refreshBtn);

        topBar.add(actionsPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"ID", "Patient", "Patient Phone", "Doctor", "Department", "Date", "Time", "Reason", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new CustomTable(tableModel);
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        appointmentTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        appointmentTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        appointmentTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        appointmentTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        appointmentTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        appointmentTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        appointmentTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        appointmentTable.getColumnModel().getColumn(8).setPreferredWidth(110);

        appointmentTable.getColumnModel().getColumn(8).setCellRenderer(new StatusBadge());

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(new LineBorder(UIUtils.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(UIUtils.CARD_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshAppointments() {
        String keyword = searchField.getText().trim();
        String status = (String) statusFilterCombo.getSelectedItem();

        currentAppointments = appointmentService.getAllAppointments(status, null, keyword);

        tableModel.setRowCount(0);
        for (Appointment appt : currentAppointments) {
            tableModel.addRow(new Object[]{
                    "#" + appt.getId(),
                    appt.getPatientName() != null ? appt.getPatientName() : "Patient #" + appt.getPatientId(),
                    appt.getPatientPhone() != null ? appt.getPatientPhone() : "-",
                    appt.getDoctorName() != null ? appt.getDoctorName() : "Doctor #" + appt.getDoctorId(),
                    appt.getDepartment(),
                    DateUtils.formatDate(appt.getAppointmentDate()),
                    appt.getAppointmentTime(),
                    appt.getReason(),
                    appt.getStatus().getDisplayName()
            });
        }

        countLabel.setText("(" + currentAppointments.size() + " appointment" + (currentAppointments.size() == 1 ? "" : "s") + ")");
    }

    private void onConfirmAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showWarning(this, "Please select an appointment from the table to confirm.");
            return;
        }

        Appointment appt = currentAppointments.get(selectedRow);
        if (appt.isConfirmed()) {
            UIUtils.showWarning(this, "This appointment is already confirmed.");
            return;
        }
        if (appt.isCancelled()) {
            UIUtils.showWarning(this, "Cannot confirm a cancelled appointment.");
            return;
        }

        String err = appointmentService.confirmAppointment(appt.getId());
        if (err == null) {
            UIUtils.showSuccess(this, "Appointment #" + appt.getId() + " confirmed successfully!");
            refreshAppointments();
            if (onDataChangedCallback != null) onDataChangedCallback.run();
        } else {
            UIUtils.showError(this, err);
        }
    }

    private void onCancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showWarning(this, "Please select an appointment from the table to cancel.");
            return;
        }

        Appointment appt = currentAppointments.get(selectedRow);
        if (appt.isCancelled()) {
            UIUtils.showWarning(this, "This appointment is already cancelled.");
            return;
        }

        boolean confirm = UIUtils.confirm(
                this,
                "Cancel Appointment #" + appt.getId() + " for " + appt.getPatientName() + " with " + appt.getDoctorName() + "?\nSlot will be freed up for rebooking.",
                "Confirm Cancellation"
        );

        if (confirm) {
            String err = appointmentService.cancelAppointment(appt.getId());
            if (err == null) {
                UIUtils.showSuccess(this, "Appointment #" + appt.getId() + " cancelled. Slot is now available.");
                refreshAppointments();
                if (onDataChangedCallback != null) onDataChangedCallback.run();
            } else {
                UIUtils.showError(this, err);
            }
        }
    }
}
