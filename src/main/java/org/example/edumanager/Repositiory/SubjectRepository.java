package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Staff attendance/notes/result pages filter by semester
    List<Subject> findBySemester(String semester);

    Optional<Subject> findBySubjectName(String subjectName);
    Optional<Subject> findBySubjectNameAndSemester(String subjectName, String semester);

    boolean existsBySubjectNameAndSemester(String subjectName, String semester);
}