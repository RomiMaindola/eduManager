package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Dto.LoginRequest;
import org.example.edumanager.Repositiory.StaffRepository;
import org.example.edumanager.Repositiory.StudentRepository;
import org.example.edumanager.Service.Imp.AuthService;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    private final BCryptPasswordEncoder encoder;

    public AuthController(AuthService authService,
                          StudentRepository studentRepo,
                          StaffRepository staffRepo,
                          BCryptPasswordEncoder encoder) {
        this.authService = authService;
        this.studentRepo = studentRepo;
        this.staffRepo = staffRepo;
        this.encoder = encoder;
    }

    // ── STUDENT REGISTER ─────────────────────────────────────────────────────
    @PostMapping("/student/register")
    @ResponseBody
    public String registerStudent(@RequestBody Student student) {
        student.setPassword(encoder.encode(student.getPassword()));
        student.setRole("ROLE_STUDENT");
        studentRepo.save(student);
        return "Student Registered Successfully";
    }

    // ── STAFF REGISTER ───────────────────────────────────────────────────────
    @PostMapping("/staff/register")
    @ResponseBody
    public String registerStaff(@RequestBody Staff staff) {
        staff.setPassword(encoder.encode(staff.getPassword()));
        staff.setRole("ROLE_STAFF");
        staffRepo.save(staff);
        return "Staff Registered Successfully";
    }

    // ── COMMON LOGIN (Thymeleaf form POST) ───────────────────────────────────
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpServletRequest httpRequest) {

        Object user = authService.authenticateUser(request);

        if (user == null) {
            // Redirect to the correct login page with error
            if (request.getAdmissionNo() != null && !request.getAdmissionNo().isBlank()) {
                return "redirect:/student/login-page?error=true";
            }
            return "redirect:/staff/login-page?error=true";
        }

        String role = (user instanceof Student) ? "ROLE_STUDENT" : "ROLE_STAFF";

        // Persist SecurityContext in session (required for session-based auth)
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user, null,
                        List.of(new SimpleGrantedAuthority(role))
                );

        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);
        session.setAttribute("user", user);
        session.setAttribute("role", role);

        return role.equals("ROLE_STUDENT")
                ? "redirect:/student/dashboard"
                : "redirect:/staff/dashboard";
    }

    // ── LOGOUT ───────────────────────────────────────────────────────────────
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}
