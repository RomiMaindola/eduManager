package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.edumanager.entity.Staff;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) {
            return "redirect:/staff/login-page";
        }
        model.addAttribute("staff", user);
        return "staff-dashboard";
    }

    @GetMapping("/attendance")
    public String attendance(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", user);
        return "staff-attendance";
    }

    @GetMapping("/notes")
    public String notes(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", user);
        return "staff-notes";
    }

    @GetMapping("/result")
    public String result(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", user);
        return "staff-result";
    }

    @GetMapping("/announcement")
    public String announcement(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", user);
        return "staff-announcement";
    }

    @GetMapping("/leave")
    public String leave(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", user);
        return "staff-leave";
    }
    @GetMapping("/generate-exam")
    public String generateExam(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        model.addAttribute("staff", user);
        return "generate-paper";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}
