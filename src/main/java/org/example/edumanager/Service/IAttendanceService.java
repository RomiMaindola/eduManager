package org.example.edumanager.Service;

import org.example.edumanager.entity.Attendance;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAttendanceService {

    // Mark / update attendance
    Attendance markAttendance(Attendance attendance);

    // Duplicate guard: check if already marked
    Optional<Attendance> findExisting(Student student, Subject subject, LocalDate date);
    boolean isAlreadyMarked(Long studentId, Long subjectId, LocalDate date);

    // Student view: full history
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByStudentNewestFirst(Long studentId);

    // Student view: per-subject attendance
    List<Attendance> findByStudentAndSubject(Long studentId, Long subjectId);

    // Attendance percentage for a student in a subject
    double getAttendancePercentage(Long studentId, Long subjectId);

    // Staff view: class attendance for a date + subject
    List<Attendance> findBySubjectAndDate(Long subjectId, LocalDate date);

    // Staff view: all records they marked
    List<Attendance> findByStaff(Staff staff);
    List<Attendance> findByStaffId(Long staffId);

    // Counts
    long countPresent(Long studentId, Long subjectId);
    long countAbsent(Long studentId, Long subjectId);
    long countTotalClassesBySubject(Long subjectId);

    void deleteById(Long id);
}