package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.edumanager.entity.Student;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentCotroller {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-dashboard";
    }

    @GetMapping("/attendance")
    public String attendance(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-attendance";
    }

    @GetMapping("/notes")
    public String notes(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-notes";
    }

    @GetMapping("/result")
    public String result(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-result";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-profile";
    }

    @GetMapping("/announcement")
    public String announcement(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-announcement";
    }

    @GetMapping("/leave")
    public String leave(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student)) return "redirect:/student/login-page";
        model.addAttribute("student", user);
        return "student-leave";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}
