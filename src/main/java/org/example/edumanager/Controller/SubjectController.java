package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Subject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/staff/subjects")
public class SubjectController {

    private final ISubjectService subjectService;

    public SubjectController(ISubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // ── GET /staff/subjects ───────────────────────────────────────────────────
    // Returns all subjects as JSON (used by JS dropdowns in attendance/result pages)
    @GetMapping
    @ResponseBody
    public List<Subject> getAllSubjects(HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return List.of();
        return subjectService.findAll();
    }

    // ── GET /staff/subjects?semester={sem} ────────────────────────────────────
    // Filter subjects by semester for the attendance/result pages' semester dropdown
    @GetMapping("/by-semester")
    @ResponseBody
    public List<Subject> getSubjectsBySemester(@RequestParam String semester, HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return List.of();
        return subjectService.findBySemester(semester);
    }

    // ── POST /staff/subjects ──────────────────────────────────────────────────
    // Create a new subject (admin action via JSON)
    @PostMapping
    @ResponseBody
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject, HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return ResponseEntity.status(403).build();

        if (subjectService.existsBySubjectNameAndSemester(subject.getSubjectName(), subject.getSemester())) {
            return ResponseEntity.badRequest().build();
        }
        Subject saved = subjectService.save(subject);
        return ResponseEntity.ok(saved);
    }

    // ── DELETE /staff/subjects/{id} ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return ResponseEntity.status(403).build();
        subjectService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}