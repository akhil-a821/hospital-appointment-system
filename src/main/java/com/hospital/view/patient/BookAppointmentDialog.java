package com.hospital.view.patient;

import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.util.DateUtils;
import com.hospital.util.SessionManager;
import com.hospital.util.UIUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Booking Dialog with Dynamic Slot Availability, Conflict Checks, and Reason input.
 */
public class BookAppointmentDialog extends JDialog {

    private final Doctor doctor;
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final Runnable onSuccessCallback;

    private JSpinner dateSpinner;
    private JPanel slotsGridPanel;
    private JTextArea reasonArea;
    private JLabel availabilityStatusLabel;
    private String selectedTimeSlot = null;
    private final List<JButton> slotButtons = new ArrayList<>();

    public BookAppointmentDialog(JFrame parent, Doctor doctor, Runnable onSuccessCallback) {
        super(parent, "Book Appointment with " + doctor.getName(), true);
        this.doctor = doctor;
        this.appointmentService = new AppointmentService();
        this.doctorService = new DoctorService();
        this.onSuccessCallback = onSuccessCallback;

        initComponents();
        updateSlotsForSelectedDate();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBackground(UIUtils.BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        mainPanel.setPreferredSize(new Dimension(560, 680));

        // 1. Doctor Header Summary Card
        JPanel doctorCard = new JPanel(new BorderLayout(12, 8));
        doctorCard.setBackground(UIUtils.CARD_BG);
        doctorCard.setBorder(new CompoundBorder(
                new LineBorder(UIUtils.BORDER_COLOR, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel docName = new JLabel(doctor.getName());
        docName.setFont(UIUtils.bold(17));
        docName.setForeground(UIUtils.TEXT_PRIMARY);

        JLabel docSpecialty = new JLabel(doctor.getDepartment() + " • " + doctor.getSpecialization());
        docSpecialty.setFont(UIUtils.medium(13));
        docSpecialty.setForeground(UIUtils.PRIMARY);

        JPanel docMeta = new JPanel(new GridLayout(2, 2, 8, 4));
        docMeta.setOpaque(false);
        docMeta.add(createMetaLabel("Available Days:", doctor.getAvailableDays()));
        docMeta.add(createMetaLabel("Working Hours:", doctor.getAvailableTime()));
        docMeta.add(createMetaLabel("Location:", doctor.getRoomNo()));
        docMeta.add(createMetaLabel("Consultation Fee:", "$" + doctor.getConsultationFee()));

        doctorCard.add(docName, BorderLayout.NORTH);
        doctorCard.add(docSpecialty, BorderLayout.CENTER);
        doctorCard.add(docMeta, BorderLayout.SOUTH);

        mainPanel.add(doctorCard, BorderLayout.NORTH);

        // 2. Booking Controls Container
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        // Date Picker Section
        JPanel dateSection = new JPanel(new BorderLayout(8, 4));
        dateSection.setOpaque(false);
        JLabel dateTitle = new JLabel("1. Select Appointment Date (Today or Future)");
        dateTitle.setFont(UIUtils.bold(13));
        dateTitle.setForeground(UIUtils.TEXT_PRIMARY);

        LocalDate initialDate = LocalDate.now().plusDays(1);
        SpinnerDateModel dateModel = new SpinnerDateModel(
                java.sql.Date.valueOf(initialDate),
                java.sql.Date.valueOf(LocalDate.now()),
                null,
                java.util.Calendar.DAY_OF_MONTH
        );
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd (EEEE)");
        dateSpinner.setEditor(editor);
        dateSpinner.setFont(UIUtils.medium(13));
        dateSpinner.setPreferredSize(new Dimension(300, 36));

        dateSpinner.addChangeListener(e -> updateSlotsForSelectedDate());

        dateSection.add(dateTitle, BorderLayout.NORTH);
        dateSection.add(dateSpinner, BorderLayout.CENTER);

        availabilityStatusLabel = new JLabel(" ");
        availabilityStatusLabel.setFont(UIUtils.medium(12));
        dateSection.add(availabilityStatusLabel, BorderLayout.SOUTH);

        bodyPanel.add(dateSection);
        bodyPanel.add(Box.createVerticalStrut(12));

        // Time Slot Section
        JPanel slotSection = new JPanel(new BorderLayout(8, 4));
        slotSection.setOpaque(false);
        JLabel slotTitle = new JLabel("2. Choose an Available Time Slot");
        slotTitle.setFont(UIUtils.bold(13));
        slotTitle.setForeground(UIUtils.TEXT_PRIMARY);

        slotsGridPanel = new JPanel(new GridLayout(0, 3, 8, 8));
        slotsGridPanel.setOpaque(false);

        JScrollPane slotScroll = new JScrollPane(slotsGridPanel);
        slotScroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR, 1, true));
        slotScroll.setPreferredSize(new Dimension(500, 140));
        slotScroll.getViewport().setBackground(UIUtils.CARD_BG);

        slotSection.add(slotTitle, BorderLayout.NORTH);
        slotSection.add(slotScroll, BorderLayout.CENTER);

        bodyPanel.add(slotSection);
        bodyPanel.add(Box.createVerticalStrut(12));

        // Reason for Visit Section
        JPanel reasonSection = new JPanel(new BorderLayout(8, 4));
        reasonSection.setOpaque(false);
        JLabel reasonTitle = new JLabel("3. Reason for Visit / Symptoms *");
        reasonTitle.setFont(UIUtils.bold(13));
        reasonTitle.setForeground(UIUtils.TEXT_PRIMARY);

        reasonArea = new JTextArea(3, 20);
        reasonArea.setFont(UIUtils.regular(13));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setBorder(new LineBorder(UIUtils.BORDER_COLOR, 1, true));

        reasonSection.add(reasonTitle, BorderLayout.NORTH);
        reasonSection.add(reasonScroll, BorderLayout.CENTER);

        bodyPanel.add(reasonSection);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        // 3. Action Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footerPanel.setOpaque(false);

        JButton cancelBtn = UIUtils.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton confirmBtn = UIUtils.createPrimaryButton("Confirm Appointment Booking");
        confirmBtn.addActionListener(e -> performBooking());

        footerPanel.add(cancelBtn);
        footerPanel.add(confirmBtn);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JLabel createMetaLabel(String title, String val) {
        JLabel l = new JLabel("<html><b>" + title + "</b> " + (val != null ? val : "N/A") + "</html>");
        l.setFont(UIUtils.regular(12));
        l.setForeground(UIUtils.TEXT_SECONDARY);
        return l;
    }

    private LocalDate getSelectedDate() {
        java.util.Date d = (java.util.Date) dateSpinner.getValue();
        return new java.sql.Date(d.getTime()).toLocalDate();
    }

    private void updateSlotsForSelectedDate() {
        LocalDate date = getSelectedDate();
        selectedTimeSlot = null;
        slotsGridPanel.removeAll();
        slotButtons.clear();

        if (DateUtils.isPastDate(date)) {
            availabilityStatusLabel.setForeground(new Color(239, 68, 68));
            availabilityStatusLabel.setText("Notice: Past dates cannot be selected.");
            slotsGridPanel.revalidate();
            slotsGridPanel.repaint();
            return;
        }

        String dayName = DateUtils.getDayOfWeekName(date);
        if (!doctor.isAvailableOnDay(dayName)) {
            availabilityStatusLabel.setForeground(new Color(239, 68, 68));
            availabilityStatusLabel.setText("Notice: " + doctor.getName() + " is not available on " + dayName + "s.");
            slotsGridPanel.revalidate();
            slotsGridPanel.repaint();
            return;
        }

        availabilityStatusLabel.setForeground(new Color(16, 185, 129));
        availabilityStatusLabel.setText("Status: " + doctor.getName() + " is available on " + dayName + ".");

        List<DoctorService.TimeSlotInfo> slots = doctorService.getAvailableSlotsForDoctorAndDate(doctor, date);
        if (slots.isEmpty()) {
            JLabel emptyLbl = new JLabel("No consultation slots configured for this day.", SwingConstants.CENTER);
            emptyLbl.setFont(UIUtils.regular(12));
            slotsGridPanel.add(emptyLbl);
        } else {
            for (DoctorService.TimeSlotInfo slot : slots) {
                JButton slotBtn = new JButton(slot.getTimeSlot());
                slotBtn.setFont(UIUtils.medium(12));
                slotBtn.setFocusPainted(false);
                slotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if (slot.isBooked()) {
                    slotBtn.setText(slot.getTimeSlot() + " (Booked)");
                    slotBtn.setEnabled(false);
                    slotBtn.setBackground(new Color(241, 245, 249));
                    slotBtn.setForeground(new Color(156, 163, 175));
                    slotBtn.setBorder(new LineBorder(new Color(229, 231, 235), 1, true));
                } else {
                    slotBtn.setBackground(Color.WHITE);
                    slotBtn.setForeground(UIUtils.PRIMARY);
                    slotBtn.setBorder(new LineBorder(new Color(186, 230, 253), 1, true));

                    slotBtn.addActionListener(e -> {
                        selectedTimeSlot = slot.getTimeSlot();
                        highlightSelectedSlot(slotBtn);
                    });
                }
                slotButtons.add(slotBtn);
                slotsGridPanel.add(slotBtn);
            }
        }

        slotsGridPanel.revalidate();
        slotsGridPanel.repaint();
    }

    private void highlightSelectedSlot(JButton selectedBtn) {
        for (JButton b : slotButtons) {
            if (b.isEnabled()) {
                b.setBackground(Color.WHITE);
                b.setForeground(UIUtils.PRIMARY);
                b.setBorder(new LineBorder(new Color(186, 230, 253), 1, true));
            }
        }
        selectedBtn.setBackground(UIUtils.PRIMARY);
        selectedBtn.setForeground(Color.WHITE);
        selectedBtn.setBorder(new LineBorder(UIUtils.PRIMARY, 1, true));
    }

    private void performBooking() {
        Patient patient = SessionManager.getCurrentPatient();
        if (patient == null) {
            UIUtils.showError(this, "Session error: Please sign in as a patient.");
            return;
        }

        LocalDate date = getSelectedDate();
        if (selectedTimeSlot == null) {
            UIUtils.showWarning(this, "Please select an available time slot.");
            return;
        }

        String reason = reasonArea.getText().trim();
        if (reason.isEmpty()) {
            UIUtils.showWarning(this, "Please describe the reason for your visit.");
            reasonArea.requestFocus();
            return;
        }

        String error = appointmentService.bookAppointment(
                patient.getId(),
                doctor.getId(),
                doctor.getDepartment(),
                date,
                selectedTimeSlot,
                reason
        );

        if (error == null) {
            UIUtils.showSuccess(this, "Appointment successfully booked for " + DateUtils.formatDate(date) + " at " + selectedTimeSlot + "!");
            dispose();
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
        } else {
            UIUtils.showError(this, error);
            updateSlotsForSelectedDate();
        }
    }
}
