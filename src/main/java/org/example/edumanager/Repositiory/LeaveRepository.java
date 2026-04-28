package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.LeaveApplication;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {

    // --- Student leave history (student-leave.html) ---
    List<LeaveApplication> findByStudent(Student student);
    List<LeaveApplication> findByStudentId(Long studentId);
    List<LeaveApplication> findByStudentOrderByFromDateDesc(Student student);
    List<LeaveApplication> findByStudentIdOrderByFromDateDesc(Long studentId);

    // Filter by status for a student
    List<LeaveApplication> findByStudentAndStatus(Student student, String status);

    // --- Staff leave history (staff-leave.html) ---
    List<LeaveApplication> findByStaff(Staff staff);
    List<LeaveApplication> findByStaffId(Long staffId);
    List<LeaveApplication> findByStaffOrderByFromDateDesc(Staff staff);
    List<LeaveApplication> findByStaffIdOrderByFromDateDesc(Long staffId);

    // Filter by status for a staff member
    List<LeaveApplication> findByStaffAndStatus(Staff staff, String status);

    // --- Admin/global queries ---
    List<LeaveApplication> findByStatus(String status);           // all pending leaves
    List<LeaveApplication> findByRole(String role);               // ROLE_STUDENT or ROLE_STAFF
    List<LeaveApplication> findByRoleAndStatus(String role, String status);
}