package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    // --- Authentication ---
    Optional<Staff> findByEmailAndStaffNo(String email, String staffNo);
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByStaffNo(String staffNo);

    // --- Filtering ---
    List<Staff> findByDepartment(String department);

    // --- Existence checks ---
    boolean existsByEmail(String email);
    boolean existsByStaffNo(String staffNo);
}