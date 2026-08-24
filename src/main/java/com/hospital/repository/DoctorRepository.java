package com.hospital.repository;

import com.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    List<Doctor> findAllByOrderByNameAsc();

    @Query("SELECT d FROM Doctor d WHERE " +
           "(:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:department IS NULL OR :department = '' OR :department = 'All Departments' OR LOWER(d.department) = LOWER(:department)) " +
           "ORDER BY d.name ASC")
    List<Doctor> searchDoctors(@Param("keyword") String keyword, @Param("department") String department);
}
