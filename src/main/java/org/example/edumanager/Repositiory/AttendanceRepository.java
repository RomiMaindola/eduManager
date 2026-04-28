package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Attendance;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Student views their own attendance (student-attendance.html)
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByStudentId(Long studentId);

    // Subject-wise attendance for a student (attendance % per subject)
    List<Attendance> findByStudentAndSubject(Student student, Subject subject);
    List<Attendance> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    // Staff loads a class's attendance for a specific date + subject
    List<Attendance> findBySubjectAndDate(Subject subject, LocalDate date);
    List<Attendance> findBySubjectIdAndDate(Long subjectId, LocalDate date);

    // Check if attendance already marked (avoid duplicates)
    Optional<Attendance> findByStudentAndSubjectAndDate(Student student, Subject subject, LocalDate date);

    // Staff views all records they marked
    List<Attendance> findByMarkedBy(Staff markedBy);
    List<Attendance> findByMarkedById(Long staffId);

    // Aggregate: count present/absent for a student per subject
    long countByStudentAndSubjectAndStatus(Student student, Subject subject, String status);
    long countByStudentIdAndSubjectIdAndStatus(Long studentId, Long subjectId, String status);

    // Total classes per subject (all records regardless of status)
    long countBySubject(Subject subject);
    long countBySubjectId(Long subjectId);

    // Recent attendance log for a student (student-attendance.html recent log)
    List<Attendance> findByStudentOrderByDateDesc(Student student);
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);
}