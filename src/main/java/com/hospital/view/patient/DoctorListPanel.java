package com.hospital.view.patient;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import com.hospital.util.UIUtils;
import com.hospital.view.MainFrame;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Doctor Directory with real-time name searching, department filtering, and appointment booking.
 */
public class DoctorListPanel extends JPanel {

    private final MainFrame mainFrame;
    private final DoctorService doctorService;
    private final DepartmentDAO departmentDAO;
    private final Runnable onAppointmentBooked;

    private JTextField searchField;
    private JComboBox<String> departmentFilterCombo;
    private JPanel doctorCardsContainer;
    private JLabel countLabel;

    public DoctorListPanel(MainFrame mainFrame, Runnable onAppointmentBooked) {
        this.mainFrame = mainFrame;
        this.doctorService = new DoctorService();
        this.departmentDAO = new DepartmentDAO();
        this.onAppointmentBooked = onAppointmentBooked;

        initComponents();
        loadDepartments();
        refreshDoctors();
    }

    private void initComponents() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UIUtils.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top Search & Filter Bar
        JPanel topBar = new JPanel(new BorderLayout(12, 12));
        topBar.setBackground(UIUtils.CARD_BG);
        topBar.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        // Title and count
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Available Doctors");
        titleLabel.setFont(UIUtils.bold(18));
        titleLabel.setForeground(UIUtils.TEXT_PRIMARY);

        countLabel = new JLabel("(0 doctors found)");
        countLabel.setFont(UIUtils.regular(13));
        countLabel.setForeground(UIUtils.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(countLabel);
        topBar.add(titlePanel, BorderLayout.WEST);

        // Search & Filter controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        JLabel searchIcon = new JLabel("Search:");
        searchIcon.setFont(UIUtils.medium(13));
        searchField = UIUtils.createTextField(16);
        searchField.putClientProperty("JTextField.placeholderText", "Search doctor by name or specialty...");

        departmentFilterCombo = new JComboBox<>(new String[]{"All Departments"});
        departmentFilterCombo.setFont(UIUtils.regular(13));
        departmentFilterCombo.setBackground(Color.WHITE);
        departmentFilterCombo.setPreferredSize(new Dimension(180, 34));

        JButton resetBtn = UIUtils.createSecondaryButton("Reset Filters");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            departmentFilterCombo.setSelectedIndex(0);
            refreshDoctors();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshDoctors(); }
            public void removeUpdate(DocumentEvent e) { refreshDoctors(); }
            public void changedUpdate(DocumentEvent e) { refreshDoctors(); }
        });

        departmentFilterCombo.addActionListener(e -> refreshDoctors());

        controlsPanel.add(searchIcon);
        controlsPanel.add(searchField);
        controlsPanel.add(new JLabel("Dept:"));
        controlsPanel.add(departmentFilterCombo);
        controlsPanel.add(resetBtn);

        topBar.add(controlsPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Doctor Cards Grid / ScrollPane
        doctorCardsContainer = new JPanel();
        doctorCardsContainer.setLayout(new BoxLayout(doctorCardsContainer, BoxLayout.Y_AXIS));
        doctorCardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(doctorCardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(UIUtils.BG_MAIN);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDepartments() {
        try {
            List<Department> departments = departmentDAO.getAllDepartments();
            for (Department d : departments) {
                departmentFilterCombo.addItem(d.getName());
            }
        } catch (SQLException e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
    }

    public void refreshDoctors() {
        String keyword = searchField.getText().trim();
        String dept = (String) departmentFilterCombo.getSelectedItem();

        List<Doctor> doctors = doctorService.searchDoctors(keyword, dept);
        countLabel.setText("(" + doctors.size() + " doctor" + (doctors.size() == 1 ? "" : "s") + " available)");

        doctorCardsContainer.removeAll();

        if (doctors.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(60, 20, 60, 20));
            JLabel emptyMsg = new JLabel("No doctors found matching the search criteria.", SwingConstants.CENTER);
            emptyMsg.setFont(UIUtils.medium(14));
            emptyMsg.setForeground(UIUtils.TEXT_MUTED);
            emptyPanel.add(emptyMsg);
            doctorCardsContainer.add(emptyPanel);
        } else {
            for (Doctor doc : doctors) {
                doctorCardsContainer.add(createDoctorCard(doc));
                doctorCardsContainer.add(Box.createVerticalStrut(12));
            }
        }

        doctorCardsContainer.revalidate();
        doctorCardsContainer.repaint();
    }

    private JPanel createDoctorCard(Doctor doc) {
        JPanel card = new JPanel(new BorderLayout(16, 12));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // Left Avatar & Info
        JPanel leftPanel = new JPanel(new BorderLayout(14, 8));
        leftPanel.setOpaque(false);

        // Info details
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(doc.getName());
        nameLabel.setFont(UIUtils.bold(16));
        nameLabel.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel specialtyLabel = new JLabel(doc.getDepartment() + " • " + doc.getSpecialization());
        specialtyLabel.setFont(UIUtils.medium(13));
        specialtyLabel.setForeground(UIUtils.PRIMARY);

        JLabel contactLabel = new JLabel("Phone: " + doc.getPhone() + "  |  Room: " + doc.getRoomNo() + "  |  Fee: $" + doc.getConsultationFee());
        contactLabel.setFont(UIUtils.regular(12));
        contactLabel.setForeground(UIUtils.TEXT_SECONDARY);

        infoPanel.add(nameLabel);
        infoPanel.add(specialtyLabel);
        infoPanel.add(contactLabel);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        card.add(leftPanel, BorderLayout.CENTER);

        // Right Schedule & Book Action
        JPanel rightPanel = new JPanel(new BorderLayout(10, 8));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(280, 80));

        JPanel schedulePanel = new JPanel(new GridLayout(2, 1, 2, 2));
        schedulePanel.setOpaque(false);

        JLabel daysLabel = new JLabel("Days: " + doc.getAvailableDays());
        daysLabel.setFont(UIUtils.regular(12));
        daysLabel.setForeground(UIUtils.TEXT_SECONDARY);

        JLabel hoursLabel = new JLabel("Hours: " + doc.getAvailableTime());
        hoursLabel.setFont(UIUtils.regular(12));
        hoursLabel.setForeground(UIUtils.TEXT_SECONDARY);

        schedulePanel.add(daysLabel);
        schedulePanel.add(hoursLabel);
        rightPanel.add(schedulePanel, BorderLayout.NORTH);

        JButton bookBtn = UIUtils.createPrimaryButton("Book Appointment");
        bookBtn.setFont(UIUtils.bold(12));
        bookBtn.addActionListener(e -> {
            BookAppointmentDialog dialog = new BookAppointmentDialog(mainFrame, doc, onAppointmentBooked);
            dialog.setVisible(true);
        });
        rightPanel.add(bookBtn, BorderLayout.SOUTH);

        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }
}
