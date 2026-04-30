package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Service.IAnnouncementService;
import org.example.edumanager.Service.IAttendanceService;
import org.example.edumanager.Service.ILeaveApplicationService;
import org.example.edumanager.Service.IResultService;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Announcement;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final IAnnouncementService announcementService;
    private final IAttendanceService attendanceService;
    private final IResultService resultService;
    private final ILeaveApplicationService leaveService;
    private final ISubjectService subjectService;

    public StudentController(IAnnouncementService announcementService,
                             IAttendanceService attendanceService,
                             IResultService resultService,
                             ILeaveApplicationService leaveService,
                             ISubjectService subjectService) {
        this.announcementService = announcementService;
        this.attendanceService = attendanceService;
        this.resultService = resultService;
        this.leaveService = leaveService;
        this.subjectService = subjectService;
    }

    // ── /student/dashboard ────────────────────────────────────────────────────
    // student-dashboard.html uses ${student.name}, ${student.email}, etc.
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        model.addAttribute("student", student);

        // Quick summary counts for dashboard widgets
        model.addAttribute("pendingLeaves",
                leaveService.findByStudentAndStatus(student.getId(), "Pending").size());
        model.addAttribute("latestAnnouncements",
                announcementService.findAllNewestFirst().stream().limit(3).toList());

        return "student-dashboard";
    }

    // ── /student/profile ──────────────────────────────────────────────────────
    // student-profile.html uses ${student.name}, ${student.email}, ${student.admissionNo},
    // ${student.dob}, ${student.branch}, ${student.semester}
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";
        model.addAttribute("student", student);
        return "student-profile";
    }

    // ── /student/logout ───────────────────────────────────────────────────────
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}