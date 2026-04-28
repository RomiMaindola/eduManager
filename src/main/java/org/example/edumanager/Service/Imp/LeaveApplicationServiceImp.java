package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.LeaveRepository;
import org.example.edumanager.Repositiory.StaffRepository;
import org.example.edumanager.Repositiory.StudentRepository;
import org.example.edumanager.Service.ILeaveApplicationService;
import org.example.edumanager.entity.LeaveApplication;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveApplicationServiceImp implements ILeaveApplicationService {

    private final LeaveRepository leaveRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    public LeaveApplicationServiceImp(LeaveRepository leaveRepository,
                                      StudentRepository studentRepository,
                                      StaffRepository staffRepository) {
        this.leaveRepository = leaveRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
    }

    @Override
    public LeaveApplication save(LeaveApplication leaveApplication) {
        if (leaveApplication.getStatus() == null || leaveApplication.getStatus().isBlank()) {
            leaveApplication.setStatus("Pending");
        }
        return leaveRepository.save(leaveApplication);
    }

    @Override
    public Optional<LeaveApplication> findById(Long id) {
        return leaveRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        leaveRepository.deleteById(id);
    }

    @Override
    public LeaveApplication updateStatus(Long id, String status) {
        LeaveApplication leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveApplication not found: " + id));
        leave.setStatus(status);
        return leaveRepository.save(leave);
    }

    // --- Student ---

    @Override
    public List<LeaveApplication> findByStudent(Student student) {
        return leaveRepository.findByStudent(student);
    }

    @Override
    public List<LeaveApplication> findByStudentId(Long studentId) {
        return leaveRepository.findByStudentId(studentId);
    }

    @Override
    public List<LeaveApplication> findByStudentNewestFirst(Long studentId) {
        return leaveRepository.findByStudentIdOrderByFromDateDesc(studentId);
    }

    @Override
    public List<LeaveApplication> findByStudentAndStatus(Long studentId, String status) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        return leaveRepository.findByStudentAndStatus(student, status);
    }

    // --- Staff ---

    @Override
    public List<LeaveApplication> findByStaff(Staff staff) {
        return leaveRepository.findByStaff(staff);
    }

    @Override
    public List<LeaveApplication> findByStaffId(Long staffId) {
        return leaveRepository.findByStaffId(staffId);
    }

    @Override
    public List<LeaveApplication> findByStaffNewestFirst(Long staffId) {
        return leaveRepository.findByStaffIdOrderByFromDateDesc(staffId);
    }

    @Override
    public List<LeaveApplication> findByStaffAndStatus(Long staffId, String status) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffId));
        return leaveRepository.findByStaffAndStatus(staff, status);
    }

    // --- Admin / Global ---

    @Override
    public List<LeaveApplication> findByStatus(String status) {
        return leaveRepository.findByStatus(status);
    }

    @Override
    public List<LeaveApplication> findByRole(String role) {
        return leaveRepository.findByRole(role);
    }

    @Override
    public List<LeaveApplication> findByRoleAndStatus(String role, String status) {
        return leaveRepository.findByRoleAndStatus(role, status);
    }
}