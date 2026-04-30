package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // --- Authentication ---
    Optional<Student> findByEmailAndAdmissionNo(String email, String admissionNo);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByAdmissionNo(String admissionNo);

    // --- Filtering (used by staff when loading students for attendance/results) ---
    List<Student> findBySemester(String semester);
    List<Student> findByBranch(String branch);
    List<Student> findBySemesterAndBranch(String semester, String branch);

    // --- Existence checks ---
    boolean existsByEmail(String email);
    boolean existsByAdmissionNo(String admissionNo);
}