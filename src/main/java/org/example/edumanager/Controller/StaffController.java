package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Service.IAnnouncementService;
import org.example.edumanager.Service.ILeaveApplicationService;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Staff;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final IAnnouncementService announcementService;
    private final ILeaveApplicationService leaveService;
    private final ISubjectService subjectService;

    public StaffController(IAnnouncementService announcementService,
                           ILeaveApplicationService leaveService,
                           ISubjectService subjectService) {
        this.announcementService = announcementService;
        this.leaveService = leaveService;
        this.subjectService = subjectService;
    }

    // ── /staff/dashboard ─────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        // Populate staff dashboard profile card (staff-dashboard.html uses ${staff.name} etc.)
        model.addAttribute("staff", staff);

        // Quick stats for the dashboard (recent leave applications by this staff)
        model.addAttribute("pendingLeaves",
                leaveService.findByStaffAndStatus(staff.getId(), "Pending").size());
        model.addAttribute("myAnnouncements",
                announcementService.findByStaffId(staff.getId()).size());

        return "staff-dashboard";
    }

    // ── /staff/logout ─────────────────────────────────────────────────────────
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    // ── /staff/generate-exam ──────────────────────────────────────────────────
    @GetMapping("/generate-exam")
    public String generateExam(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", staff);
        return "generate-paper";
    }
}