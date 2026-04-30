package org.example.edumanager.Service;

import org.example.edumanager.entity.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentService {

    // CRUD
    Student save(Student student);
    Optional<Student> findById(Long id);
    List<Student> findAll();
    void deleteById(Long id);

    // Filtering
    List<Student> findBySemester(String semester);
    List<Student> findByBranch(String branch);
    List<Student> findBySemesterAndBranch(String semester, String branch);

    // Lookups
    Optional<Student> findByEmail(String email);
    Optional<Student> findByAdmissionNo(String admissionNo);

    // Existence
    boolean existsByEmail(String email);
    boolean existsByAdmissionNo(String admissionNo);
}