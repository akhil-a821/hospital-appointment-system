// =========================================================
// CarePulse Hospital Scheduling System - Frontend JS
// =========================================================

// Modal Handling
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('show');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('show');
    }
}

// Booking Dialog
let currentDoctorId = null;
let currentDoctorName = '';
let currentDoctorDept = '';
let currentDoctorDays = '';

function openBookingModal(docId, docName, dept, days, time, room, fee) {
    currentDoctorId = docId;
    currentDoctorName = docName;
    currentDoctorDept = dept;
    currentDoctorDays = days;

    document.getElementById('bookingDocId').value = docId;
    document.getElementById('bookingDept').value = dept;
    document.getElementById('bookingDocName').innerText = docName;
    document.getElementById('bookingDocDetails').innerText = dept + ' | Room: ' + room + ' | Fee: $' + fee;
    document.getElementById('bookingDocDays').innerText = 'Available Days: ' + days + ' (' + time + ')';

    // Set default date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateInput = document.getElementById('appointmentDateInput');
    dateInput.value = tomorrow.toISOString().split('T')[0];
    dateInput.min = new Date().toISOString().split('T')[0];

    loadAvailableSlots();
    openModal('bookingModal');
}

async function loadAvailableSlots() {
    const dateInput = document.getElementById('appointmentDateInput');
    const slotsContainer = document.getElementById('slotsContainer');
    const selectedSlotInput = document.getElementById('selectedTimeSlotInput');
    const availabilityNotice = document.getElementById('availabilityNotice');
    
    selectedSlotInput.value = '';
    slotsContainer.innerHTML = '<p style="color: var(--text-muted); font-size: 0.85rem;">Loading available slots...</p>';
    availabilityNotice.innerText = '';

    const selectedDate = dateInput.value;
    if (!selectedDate || !currentDoctorId) return;

    try {
        const response = await fetch(`/api/doctors/${currentDoctorId}/slots?date=${selectedDate}`);
        const slots = await response.json();

        slotsContainer.innerHTML = '';
        if (!slots || slots.length === 0) {
            slotsContainer.innerHTML = '<p style="color: #ef4444; font-size: 0.85rem;">Doctor is not available on this date/day.</p>';
            return;
        }

        slots.forEach(slot => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'slot-btn' + (slot.booked ? ' booked' : '');
            btn.innerText = slot.timeSlot + (slot.booked ? ' (Booked)' : '');
            
            if (slot.booked) {
                btn.disabled = true;
            } else {
                btn.onclick = () => {
                    document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
                    btn.classList.add('selected');
                    selectedSlotInput.value = slot.timeSlot;
                };
            }
            slotsContainer.appendChild(btn);
        });

    } catch (e) {
        slotsContainer.innerHTML = '<p style="color: #ef4444; font-size: 0.85rem;">Could not load appointment slots.</p>';
    }
}

// Doctor Edit Modal (Admin)
function openDoctorModal(doc = null) {
    const modal = document.getElementById('doctorModal');
    if (!modal) return;

    if (doc) {
        document.getElementById('docModalTitle').innerText = 'Edit Doctor Profile';
        document.getElementById('docIdInput').value = doc.id;
        document.getElementById('docNameInput').value = doc.name;
        document.getElementById('docEmailInput').value = doc.email || '';
        document.getElementById('docPhoneInput').value = doc.phone;
        document.getElementById('docSpecInput').value = doc.specialization;
        document.getElementById('docDeptSelect').value = doc.department;
        document.getElementById('docDaysInput').value = doc.availableDays;
        document.getElementById('docTimeInput').value = doc.availableTime;
        document.getElementById('docRoomInput').value = doc.roomNo;
        document.getElementById('docFeeInput').value = doc.consultationFee;
    } else {
        document.getElementById('docModalTitle').innerText = 'Add New Doctor';
        document.getElementById('docIdInput').value = '';
        document.getElementById('docNameInput').value = '';
        document.getElementById('docEmailInput').value = '';
        document.getElementById('docPhoneInput').value = '';
        document.getElementById('docSpecInput').value = '';
        document.getElementById('docDaysInput').value = 'Monday,Tuesday,Wednesday,Thursday,Friday';
        document.getElementById('docTimeInput').value = '09:00 - 17:00';
        document.getElementById('docRoomInput').value = 'Room 101';
        document.getElementById('docFeeInput').value = '50.00';
    }

    openModal('doctorModal');
}
