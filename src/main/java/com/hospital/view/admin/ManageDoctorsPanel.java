package com.hospital.view.admin;

import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import com.hospital.util.UIUtils;
import com.hospital.view.common.CustomTable;

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
 * Admin Panel for Managing Hospital Doctors (CRUD: View, Search, Add, Edit, Delete).
 */
public class ManageDoctorsPanel extends JPanel {

    private final JFrame parentFrame;
    private final DoctorService doctorService;
    private final Runnable onDataChangedCallback;

    private CustomTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel countLabel;
    private List<Doctor> currentDoctors;

    public ManageDoctorsPanel(JFrame parentFrame, Runnable onDataChangedCallback) {
        this.parentFrame = parentFrame;
        this.doctorService = new DoctorService();
        this.onDataChangedCallback = onDataChangedCallback;

        initComponents();
        refreshDoctors();
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
        JLabel titleLabel = new JLabel("Doctor Management");
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        countLabel = new JLabel("(0 doctors)");
        countLabel.setFont(UIUtils.regular(13));
        countLabel.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(countLabel);
        topBar.add(titlePanel, BorderLayout.WEST);

        // Search & Action Buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(UIUtils.medium(13));
        searchField = UIUtils.createTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search doctor...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshDoctors(); }
            public void removeUpdate(DocumentEvent e) { refreshDoctors(); }
            public void changedUpdate(DocumentEvent e) { refreshDoctors(); }
        });

        JButton addDocBtn = UIUtils.createPrimaryButton("+ Add Doctor");
        addDocBtn.addActionListener(e -> onAddDoctor());

        JButton editDocBtn = UIUtils.createSecondaryButton("Edit Doctor");
        editDocBtn.addActionListener(e -> onEditDoctor());

        JButton deleteDocBtn = UIUtils.createDangerButton("Delete Doctor");
        deleteDocBtn.addActionListener(e -> onDeleteDoctor());

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> refreshDoctors());

        actionsPanel.add(searchIcon);
        actionsPanel.add(searchField);
        actionsPanel.add(addDocBtn);
        actionsPanel.add(editDocBtn);
        actionsPanel.add(deleteDocBtn);
        actionsPanel.add(refreshBtn);

        topBar.add(actionsPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"ID", "Name", "Specialization", "Department", "Phone", "Days", "Hours", "Room", "Fee"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        doctorTable = new CustomTable(tableModel);
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        doctorTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        doctorTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        doctorTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        doctorTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        doctorTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        doctorTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        doctorTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        doctorTable.getColumnModel().getColumn(8).setPreferredWidth(70);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(new LineBorder(UIUtils.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(UIUtils.CARD_BG);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshDoctors() {
        String keyword = searchField.getText().trim();
        currentDoctors = doctorService.searchDoctors(keyword, null);

        tableModel.setRowCount(0);
        for (Doctor doc : currentDoctors) {
            tableModel.addRow(new Object[]{
                    "#" + doc.getId(),
                    doc.getName(),
                    doc.getSpecialization(),
                    doc.getDepartment(),
                    doc.getPhone(),
                    doc.getAvailableDays(),
                    doc.getAvailableTime(),
                    doc.getRoomNo(),
                    "$" + doc.getConsultationFee()
            });
        }

        countLabel.setText("(" + currentDoctors.size() + " doctor" + (currentDoctors.size() == 1 ? "" : "s") + ")");
    }

    private void onAddDoctor() {
        DoctorFormDialog dialog = new DoctorFormDialog(parentFrame, null, () -> {
            refreshDoctors();
            if (onDataChangedCallback != null) onDataChangedCallback.run();
        });
        dialog.setVisible(true);
    }

    private void onEditDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showWarning(this, "Please select a doctor to edit from the table.");
            return;
        }

        Doctor selectedDoc = currentDoctors.get(selectedRow);
        DoctorFormDialog dialog = new DoctorFormDialog(parentFrame, selectedDoc, () -> {
            refreshDoctors();
            if (onDataChangedCallback != null) onDataChangedCallback.run();
        });
        dialog.setVisible(true);
    }

    private void onDeleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showWarning(this, "Please select a doctor to delete from the table.");
            return;
        }

        Doctor selectedDoc = currentDoctors.get(selectedRow);
        boolean confirm = UIUtils.confirm(
                this,
                "Are you sure you want to delete " + selectedDoc.getName() + " (" + selectedDoc.getDepartment() + ")?",
                "Confirm Doctor Deletion"
        );

        if (confirm) {
            String err = doctorService.deleteDoctor(selectedDoc.getId());
            if (err == null) {
                UIUtils.showSuccess(this, selectedDoc.getName() + " was deleted successfully.");
                refreshDoctors();
                if (onDataChangedCallback != null) onDataChangedCallback.run();
            } else {
                UIUtils.showError(this, err);
            }
        }
    }
}
