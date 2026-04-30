package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Service.IAnnouncementService;
import org.example.edumanager.entity.Announcement;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AnnouncementController {

    private final IAnnouncementService announcementService;

    public AnnouncementController(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // ── STAFF: GET /staff/announcement ────────────────────────────────────────
    // Loads the staff-announcement.html page with the staff's own announcements
    @GetMapping("/staff/announcement")
    public String staffAnnouncementPage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<Announcement> myAnnouncements = announcementService.findByStaffNewestFirst(staff);
        model.addAttribute("staff", staff);
        model.addAttribute("announcements", myAnnouncements);
        return "staff-announcement";
    }

    // ── STAFF: POST /staff/announcement ──────────────────────────────────────
    // Handles the "Post Announcement" form submission from staff-announcement.html
    @PostMapping("/staff/announcement")
    public String postAnnouncement(@RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam(defaultValue = "All Students") String audience,
                                   @RequestParam(defaultValue = "Normal") String priority,
                                   HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setPriority(priority);
        announcement.setCreatedBy(staff);
        announcement.setCreatedAt(LocalDateTime.now());
        announcementService.save(announcement);

        return "redirect:/staff/announcement?success=true";
    }

    // ── STAFF: DELETE /staff/announcement/{id} ────────────────────────────────
    // The 🗑️ delete button in staff-announcement.html calls this
    @PostMapping("/staff/announcement/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        announcementService.deleteById(id);
        return "redirect:/staff/announcement";
    }

    // ── STUDENT: GET /student/announcement ───────────────────────────────────
    // Loads the student-announcement.html page with all announcements newest-first
    @GetMapping("/student/announcement")
    public String studentAnnouncementPage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        List<Announcement> allAnnouncements = announcementService.findAllNewestFirst();
        model.addAttribute("student", student);
        model.addAttribute("announcements", allAnnouncements);
        return "student-announcement";
    }
}