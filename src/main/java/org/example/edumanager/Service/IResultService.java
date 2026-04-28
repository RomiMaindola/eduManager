package org.example.edumanager.Service;

import org.example.edumanager.entity.Result;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;

import java.util.List;
import java.util.Optional;

public interface IResultService {

    // Save / update result
    Result save(Result result);
    Optional<Result> findById(Long id);
    void deleteById(Long id);

    // Duplicate guard
    Optional<Result> findExisting(Long studentId, Long subjectId, String semester, String examType);
    boolean existsResult(Long studentId, Long subjectId, String semester, String examType);

    // Student view
    List<Result> findByStudent(Student student);
    List<Result> findByStudentId(Long studentId);
    List<Result> findByStudentAndSemester(Long studentId, String semester);

    // Staff view: results for a subject + semester + examType
    List<Result> findBySubjectSemesterExamType(Long subjectId, String semester, String examType);

    // All results for a subject
    List<Result> findBySubject(Subject subject);
    List<Result> findBySubjectId(Long subjectId);
}