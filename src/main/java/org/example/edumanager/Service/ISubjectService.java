package org.example.edumanager.Service;

import org.example.edumanager.entity.Subject;

import java.util.List;
import java.util.Optional;

public interface ISubjectService {

    // CRUD
    Subject save(Subject subject);
    Optional<Subject> findById(Long id);
    List<Subject> findAll();
    void deleteById(Long id);

    // Filtering
    List<Subject> findBySemester(String semester);

    // Lookups
    Optional<Subject> findBySubjectName(String subjectName);
    Optional<Subject> findBySubjectNameAndSemester(String subjectName, String semester);

    // Existence
    boolean existsBySubjectNameAndSemester(String subjectName, String semester);
}