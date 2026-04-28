package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Service.ILeaveApplicationService;
import org.example.edumanager.entity.LeaveApplication;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class LeaveApplicationController {

    private final ILeaveApplicationService leaveService;

    public LeaveApplicationController(ILeaveApplicationService leaveService) {
        this.leaveService = leaveService;
    }

    // ── STAFF: GET /staff/leave ───────────────────────────────────────────────
    // Renders staff-leave.html with the staff's own leave history
    @GetMapping("/staff/leave")
    public String staffLeavePage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<LeaveApplication> history = leaveService.findByStaffNewestFirst(staff.getId());
        model.addAttribute("staff", staff);
        model.addAttribute("leaveHistory", history);
        return "staff-leave";
    }

    // ── STAFF: POST /staff/leave ──────────────────────────────────────────────
    // "📨 Submit Application" from staff-leave.html
    @PostMapping("/staff/leave")
    public String submitStaffLeave(@RequestParam String leaveType,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                   @RequestParam String reason,
                                   HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        LeaveApplication leave = new LeaveApplication();
        leave.setStaff(staff);
        leave.setRole("ROLE_STAFF");
        leave.setLeaveType(leaveType);
        leave.setFromDate(fromDate);
        leave.setToDate(toDate);
        leave.setReason(reason);
        leave.setStatus("Pending");
        leaveService.save(leave);

        return "redirect:/staff/leave?submitted=true";
    }

    // ── STUDENT: GET /student/leave ───────────────────────────────────────────
    // Renders student-leave.html with the student's own leave history
    @GetMapping("/student/leave")
    public String studentLeavePage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        List<LeaveApplication> history = leaveService.findByStudentNewestFirst(student.getId());
        model.addAttribute("student", student);
        model.addAttribute("leaveHistory", history);
        return "student-leave";
    }

    // ── STUDENT: POST /student/leave ──────────────────────────────────────────
    // "📨 Submit Application" from student-leave.html
    @PostMapping("/student/leave")
    public String submitStudentLeave(@RequestParam String leaveType,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                     @RequestParam String reason,
                                     HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        LeaveApplication leave = new LeaveApplication();
        leave.setStudent(student);
        leave.setRole("ROLE_STUDENT");
        leave.setLeaveType(leaveType);
        leave.setFromDate(fromDate);
        leave.setToDate(toDate);
        leave.setReason(reason);
        leave.setStatus("Pending");
        leaveService.save(leave);

        return "redirect:/student/leave?submitted=true";
    }

    // ── ADMIN: POST /admin/leave/{id}/status ──────────────────────────────────
    // Approve or Reject a leave application (future admin page hook)
    @PostMapping("/admin/leave/{id}/status")
    public String updateLeaveStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    HttpSession session) {
        leaveService.updateStatus(id, status);
        return "redirect:/admin/leave";
    }
}