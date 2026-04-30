package org.example.edumanager.Service;

import org.example.edumanager.entity.LeaveApplication;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;

import java.util.List;
import java.util.Optional;

public interface ILeaveApplicationService {

    // Submit / update
    LeaveApplication save(LeaveApplication leaveApplication);
    Optional<LeaveApplication> findById(Long id);
    void deleteById(Long id);

    // Approve / Reject (admin action)
    LeaveApplication updateStatus(Long id, String status);

    // Student leave history
    List<LeaveApplication> findByStudent(Student student);
    List<LeaveApplication> findByStudentId(Long studentId);
    List<LeaveApplication> findByStudentNewestFirst(Long studentId);
    List<LeaveApplication> findByStudentAndStatus(Long studentId, String status);

    // Staff leave history
    List<LeaveApplication> findByStaff(Staff staff);
    List<LeaveApplication> findByStaffId(Long staffId);
    List<LeaveApplication> findByStaffNewestFirst(Long staffId);
    List<LeaveApplication> findByStaffAndStatus(Long staffId, String status);

    // Admin / global queries
    List<LeaveApplication> findByStatus(String status);         // e.g. all "Pending"
    List<LeaveApplication> findByRole(String role);             // ROLE_STUDENT or ROLE_STAFF
    List<LeaveApplication> findByRoleAndStatus(String role, String status);
}