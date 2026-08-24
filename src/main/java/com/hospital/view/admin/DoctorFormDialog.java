package com.hospital.view.admin;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import com.hospital.util.UIUtils;
import com.hospital.util.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal dialog for Adding or Editing Doctor profiles.
 */
public class DoctorFormDialog extends JDialog {

    private final Doctor existingDoctor;
    private final DoctorService doctorService;
    private final DepartmentDAO departmentDAO;
    private final Runnable onSuccessCallback;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField specField;
    private JComboBox<Department> deptCombo;
    private JCheckBox[] dayCheckBoxes;
    private JTextField timeField;
    private JTextField roomField;
    private JTextField feeField;
    private JLabel errorLabel;

    private static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public DoctorFormDialog(JFrame parent, Doctor existingDoctor, Runnable onSuccessCallback) {
        super(parent, existingDoctor == null ? "Add New Doctor" : "Edit Doctor Profile", true);
        this.existingDoctor = existingDoctor;
        this.doctorService = new DoctorService();
        this.departmentDAO = new DepartmentDAO();
        this.onSuccessCallback = onSuccessCallback;

        initComponents();
        loadDepartments();
        if (existingDoctor != null) {
            populateFields(existingDoctor);
        }
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(UIUtils.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));
        mainPanel.setPreferredSize(new Dimension(540, 640));

        // Header
        JLabel titleLabel = new JLabel(existingDoctor == null ? "Add New Doctor" : "Edit Doctor Details");
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = UIUtils.createTextField(20);
        emailField = UIUtils.createTextField(20);
        phoneField = UIUtils.createTextField(20);
        specField = UIUtils.createTextField(20);

        deptCombo = new JComboBox<>();
        deptCombo.setFont(UIUtils.regular(13));
        deptCombo.setBackground(Color.WHITE);

        // Days Checkboxes Panel
        JPanel daysPanel = new JPanel(new GridLayout(2, 4, 4, 4));
        daysPanel.setOpaque(false);
        dayCheckBoxes = new JCheckBox[DAYS_OF_WEEK.length];
        for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
            dayCheckBoxes[i] = new JCheckBox(DAYS_OF_WEEK[i].substring(0, 3));
            dayCheckBoxes[i].setActionCommand(DAYS_OF_WEEK[i]);
            dayCheckBoxes[i].setFont(UIUtils.regular(12));
            dayCheckBoxes[i].setOpaque(false);
            if (i < 5) { // default Mon-Fri
                dayCheckBoxes[i].setSelected(true);
            }
            daysPanel.add(dayCheckBoxes[i]);
        }

        timeField = UIUtils.createTextField(20);
        timeField.setText("09:00 - 17:00");

        roomField = UIUtils.createTextField(20);
        roomField.setText("Room 101");

        feeField = UIUtils.createTextField(20);
        feeField.setText("50.00");

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Full Name *", nameField);
        addFormRow(formPanel, gbc, row++, "Email Address", emailField);
        addFormRow(formPanel, gbc, row++, "Phone Number *", phoneField);
        addFormRow(formPanel, gbc, row++, "Specialization *", specField);
        addFormRow(formPanel, gbc, row++, "Department *", deptCombo);
        addFormRow(formPanel, gbc, row++, "Available Days *", daysPanel);
        addFormRow(formPanel, gbc, row++, "Working Hours *", timeField);
        addFormRow(formPanel, gbc, row++, "Room / Clinic", roomField);
        addFormRow(formPanel, gbc, row++, "Consultation Fee ($)", feeField);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIUtils.medium(12));
        errorLabel.setForeground(new Color(239, 68, 68));
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formPanel.add(errorLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerPanel.setOpaque(false);

        JButton cancelBtn = UIUtils.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = UIUtils.createPrimaryButton(existingDoctor == null ? "Add Doctor" : "Save Changes");
        saveBtn.addActionListener(e -> onSaveDoctor());

        footerPanel.add(cancelBtn);
        footerPanel.add(saveBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
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

    private void loadDepartments() {
        try {
            List<Department> departments = departmentDAO.getAllDepartments();
            for (Department d : departments) {
                deptCombo.addItem(d);
            }
        } catch (SQLException e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
    }

    private void populateFields(Doctor doc) {
        nameField.setText(doc.getName());
        emailField.setText(doc.getEmail());
        phoneField.setText(doc.getPhone());
        specField.setText(doc.getSpecialization());
        timeField.setText(doc.getAvailableTime());
        roomField.setText(doc.getRoomNo());
        feeField.setText(doc.getConsultationFee() != null ? doc.getConsultationFee().toString() : "50.00");

        // Select department in combo
        for (int i = 0; i < deptCombo.getItemCount(); i++) {
            Department d = deptCombo.getItemAt(i);
            if (d.getName().equalsIgnoreCase(doc.getDepartment())) {
                deptCombo.setSelectedIndex(i);
                break;
            }
        }

        // Set days
        List<String> docDays = doc.getAvailableDaysList();
        for (JCheckBox cb : dayCheckBoxes) {
            cb.setSelected(docDays.stream().anyMatch(d -> d.equalsIgnoreCase(cb.getActionCommand())));
        }
    }

    private void onSaveDoctor() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String spec = specField.getText().trim();
        Department selectedDept = (Department) deptCombo.getSelectedItem();
        String deptName = selectedDept != null ? selectedDept.getName() : "";
        Integer deptId = selectedDept != null ? selectedDept.getId() : null;

        List<String> selectedDays = new ArrayList<>();
        for (JCheckBox cb : dayCheckBoxes) {
            if (cb.isSelected()) {
                selectedDays.add(cb.getActionCommand());
            }
        }
        String daysStr = String.join(",", selectedDays);
        String timeStr = timeField.getText().trim();
        String room = roomField.getText().trim();

        BigDecimal fee = new BigDecimal("50.00");
        if (ValidationUtils.isNotEmpty(feeField.getText())) {
            try {
                fee = new BigDecimal(feeField.getText().trim());
            } catch (Exception ex) {
                errorLabel.setText("Invalid fee amount. Example: 75.00");
                return;
            }
        }

        Doctor doc = (existingDoctor != null) ? existingDoctor : new Doctor();
        doc.setName(name);
        doc.setEmail(email);
        doc.setPhone(phone);
        doc.setSpecialization(spec);
        doc.setDepartmentId(deptId);
        doc.setDepartment(deptName);
        doc.setAvailableDays(daysStr);
        doc.setAvailableTime(timeStr);
        doc.setRoomNo(room);
        doc.setConsultationFee(fee);

        String error = doctorService.saveDoctor(doc);
        if (error == null) {
            UIUtils.showSuccess(this, "Doctor details successfully saved!");
            dispose();
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
        } else {
            errorLabel.setText(error);
        }
    }
}
