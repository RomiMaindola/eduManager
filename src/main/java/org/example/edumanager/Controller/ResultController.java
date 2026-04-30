package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Service.IResultService;
import org.example.edumanager.Service.IStudentService;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Result;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ResultController {

    private final IResultService resultService;
    private final IStudentService studentService;
    private final ISubjectService subjectService;

    public ResultController(IResultService resultService,
                            IStudentService studentService,
                            ISubjectService subjectService) {
        this.resultService = resultService;
        this.studentService = studentService;
        this.subjectService = subjectService;
    }

    // ── STAFF: GET /staff/result ──────────────────────────────────────────────
    // Renders staff-result.html; pre-loads subjects for the dropdowns
    @GetMapping("/staff/result")
    public String staffResultPage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("staff", staff);
        model.addAttribute("subjects", subjects);
        return "staff-result";
    }

    // ── STAFF: GET /staff/result/load ─────────────────────────────────────────
    // "Load Students" button: fetches students for given subjectId + semester + examType
    @GetMapping("/staff/result/load")
    public String loadResultStudents(@RequestParam Long subjectId,
                                     @RequestParam String semester,
                                     @RequestParam String examType,
                                     HttpSession session,
                                     Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<Student> students = studentService.findBySemester(semester);
        List<Result> existingResults = resultService.findBySubjectSemesterExamType(subjectId, semester, examType);
        List<Subject> subjects = subjectService.findAll();

        model.addAttribute("staff", staff);
        model.addAttribute("subjects", subjects);
        model.addAttribute("students", students);
        model.addAttribute("existingResults", existingResults);
        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedExamType", examType);
        return "staff-result";
    }

    // ── STAFF: POST /staff/result/save ────────────────────────────────────────
    // "💾 Save Results" button submits marks for all students
    @PostMapping("/staff/result/save")
    public String saveResults(@RequestParam Long subjectId,
                              @RequestParam String semester,
                              @RequestParam String examType,
                              @RequestParam List<Long> studentIds,
                              @RequestParam List<Integer> marksList,
                              HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";

        Subject subject = subjectService.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        for (int i = 0; i < studentIds.size(); i++) {
            Long studentId = studentIds.get(i);
            int marks = marksList.get(i);
            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

            // Upsert: update if already exists, else create new
            Optional<Result> existing = resultService.findExisting(studentId, subjectId, semester, examType);
            Result result = existing.orElse(new Result());
            result.setStudent(student);
            result.setSubject(subject);
            result.setSemester(semester);
            result.setExamType(examType);
            result.setMarks(marks);
            result.setGrade(computeGrade(marks));
            resultService.save(result);
        }

        return "redirect:/staff/result?saved=true";
    }

    // ── STUDENT: GET /student/result ──────────────────────────────────────────
    // Renders student-result.html with the student's subject-wise results
    @GetMapping("/student/result")
    public String studentResultPage(@RequestParam(required = false, defaultValue = "") String semester,
                                    HttpSession session,
                                    Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        List<Result> results;
        if (semester.isBlank()) {
            results = resultService.findByStudentId(student.getId());
        } else {
            results = resultService.findByStudentAndSemester(student.getId(), semester);
        }

        // Compute summary stats for the summary cards
        double avgMarks = results.stream().mapToInt(Result::getMarks).average().orElse(0);
        double cgpa = avgMarks / 10.0;
        String overallGrade = computeGrade((int) avgMarks);

        model.addAttribute("student", student);
        model.addAttribute("results", results);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("avgMarks", Math.round(avgMarks));
        model.addAttribute("overallGrade", overallGrade);
        model.addAttribute("cgpa", String.format("%.1f", cgpa));
        return "student-result";
    }

    // ── Grade helper ──────────────────────────────────────────────────────────
    private String computeGrade(int marks) {
        if (marks >= 85) return "A";
        if (marks >= 70) return "B";
        if (marks >= 50) return "C";
        return "F";
    }
}