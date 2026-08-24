package com.hospital.controller;

import com.hospital.model.Doctor;
import com.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/doctors/{id}/slots")
    public ResponseEntity<List<DoctorService.TimeSlotInfo>> getDoctorSlots(
            @PathVariable("id") Integer doctorId,
            @RequestParam("date") String dateStr) {

        Optional<Doctor> docOpt = doctorService.getDoctorById(doctorId);
        if (docOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            LocalDate date = LocalDate.parse(dateStr);
            List<DoctorService.TimeSlotInfo> slots = doctorService.getAvailableSlotsForDoctorAndDate(docOpt.get(), date);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
