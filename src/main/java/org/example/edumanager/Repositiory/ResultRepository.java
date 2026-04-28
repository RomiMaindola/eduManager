package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Result;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    // Student views their results (student-result.html — subject-wise table)
    List<Result> findByStudent(Student student);
    List<Result> findByStudentId(Long studentId);

    // Filter by semester (semester dropdown in student-result.html)
    List<Result> findByStudentAndSemester(Student student, String semester);
    List<Result> findByStudentIdAndSemester(Long studentId, String semester);

    // Staff loads results for a subject + semester + examType
    List<Result> findBySubjectAndSemesterAndExamType(Subject subject, String semester, String examType);
    List<Result> findBySubjectIdAndSemesterAndExamType(Long subjectId, String semester, String examType);

    // Check if result already exists (avoid duplicate entries)
    Optional<Result> findByStudentAndSubjectAndSemesterAndExamType(
            Student student, Subject subject, String semester, String examType);

    // All results for a specific subject (staff viewing all students in one exam)
    List<Result> findBySubject(Subject subject);
    List<Result> findBySubjectId(Long subjectId);
}