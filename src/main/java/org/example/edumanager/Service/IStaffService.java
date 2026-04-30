package org.example.edumanager.Service;

import org.example.edumanager.entity.Staff;

import java.util.List;
import java.util.Optional;

public interface IStaffService {

    // CRUD
    Staff save(Staff staff);
    Optional<Staff> findById(Long id);
    List<Staff> findAll();
    void deleteById(Long id);

    // Filtering
    List<Staff> findByDepartment(String department);

    // Lookups
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByStaffNo(String staffNo);

    // Existence
    boolean existsByEmail(String email);
    boolean existsByStaffNo(String staffNo);
}