package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Dto.PaperGenerationRequest;
import org.example.edumanager.Service.IPaperGenerationService;
import org.example.edumanager.entity.Staff;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Handles navigation to the exam-paper generation page (GET)
 * and the actual AI generation call (POST /api/generate-paper).
 *
 * Only authenticated Staff members may access these endpoints.
 */
@Controller
public class PaperGenerationController {

    private final IPaperGenerationService paperGenerationService;

    public PaperGenerationController(IPaperGenerationService paperGenerationService) {
        this.paperGenerationService = paperGenerationService;
    }

    // ── GET: render the generate-paper Thymeleaf page ─────────────────────────

    @GetMapping("/staff/generate-exam")
    public String generateExamPage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) {
            return "redirect:/staff/login-page";
        }
        model.addAttribute("staffName", staff.getName());
        return "generate-paper";   // resolves to templates/generate-paper.html
    }

    // ── POST: call OpenAI and return the paper as JSON ─────────────────────────

    /**
     * Accepts the form data as JSON, forwards it to the service layer
     * which calls OpenAI, and returns the generated paper text.
     *
     * Response body:
     *   { "paper": "<full exam paper text>" }
     *
     * Error response:
     *   { "error": "<message>" }
     */
    @PostMapping("/api/generate-paper")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generatePaper(
            @RequestBody PaperGenerationRequest request,
            HttpSession session) {

        // Guard: only logged-in staff
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized. Please log in as staff."));
        }

        // Validate required fields
        if (request.getSubject() == null || request.getSubject().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Subject is required."));
        }
        if (request.getSyllabus() == null || request.getSyllabus().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Syllabus / Topics are required."));
        }
        if (request.getTotalQuestions() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Total questions must be greater than 0."));
        }
        if (request.getTotalMarks() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Total marks must be greater than 0."));
        }

        try {
            String paper = paperGenerationService.generatePaper(request);
            return ResponseEntity.ok(Map.of("paper", paper));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate paper: " + e.getMessage()));
        }
    }
}