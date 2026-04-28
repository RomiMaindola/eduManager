package org.example.edumanager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 🔹 HOME
    @GetMapping("/")
    public String home() {
        return "role-selection";
    }

    // 🔹 LOGIN PAGES
    @GetMapping("/student/login-page")
    public String studentLogin() {
        return "student-login";
    }

    @GetMapping("/staff/login-page")
    public String staffLogin() {
        return "staff-login";
    }
}
