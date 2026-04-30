package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.AttendanceRepository;
import org.example.edumanager.Repositiory.StudentRepository;
import org.example.edumanager.Repositiory.SubjectRepository;
import org.example.edumanager.Service.IAttendanceService;
import org.example.edumanager.entity.Attendance;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImp implements IAttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public AttendanceServiceImp(AttendanceRepository attendanceRepository,
                                StudentRepository studentRepository,
                                SubjectRepository subjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Attendance markAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public Optional<Attendance> findExisting(Student student, Subject subject, LocalDate date) {
        return attendanceRepository.findByStudentAndSubjectAndDate(student, subject, date);
    }

    @Override
    public boolean isAlreadyMarked(Long studentId, Long subjectId, LocalDate date) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));
        return attendanceRepository.findByStudentAndSubjectAndDate(student, subject, date).isPresent();
    }

    @Override
    public List<Attendance> findByStudent(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    @Override
    public List<Attendance> findByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    @Override
    public List<Attendance> findByStudentNewestFirst(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    @Override
    public List<Attendance> findByStudentAndSubject(Long studentId, Long subjectId) {
        return attendanceRepository.findByStudentIdAndSubjectId(studentId, subjectId);
    }

    @Override
    public double getAttendancePercentage(Long studentId, Long subjectId) {
        long totalClasses = attendanceRepository.countBySubjectId(subjectId);
        if (totalClasses == 0) return 0.0;
        long present = attendanceRepository.countByStudentIdAndSubjectIdAndStatus(studentId, subjectId, "Present");
        return (present * 100.0) / totalClasses;
    }

    @Override
    public List<Attendance> findBySubjectAndDate(Long subjectId, LocalDate date) {
        return attendanceRepository.findBySubjectIdAndDate(subjectId, date);
    }

    @Override
    public List<Attendance> findByStaff(Staff staff) {
        return attendanceRepository.findByMarkedBy(staff);
    }

    @Override
    public List<Attendance> findByStaffId(Long staffId) {
        return attendanceRepository.findByMarkedById(staffId);
    }

    @Override
    public long countPresent(Long studentId, Long subjectId) {
        return attendanceRepository.countByStudentIdAndSubjectIdAndStatus(studentId, subjectId, "Present");
    }

    @Override
    public long countAbsent(Long studentId, Long subjectId) {
        return attendanceRepository.countByStudentIdAndSubjectIdAndStatus(studentId, subjectId, "Absent");
    }

    @Override
    public long countTotalClassesBySubject(Long subjectId) {
        return attendanceRepository.countBySubjectId(subjectId);
    }

    @Override
    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }
}