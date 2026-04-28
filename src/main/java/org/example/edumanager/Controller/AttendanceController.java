package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Dto.AttendanceRequest;
import org.example.edumanager.Service.IAttendanceService;
import org.example.edumanager.Service.IStudentService;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Attendance;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AttendanceController {

    private final IAttendanceService attendanceService;
    private final IStudentService studentService;
    private final ISubjectService subjectService;

    public AttendanceController(IAttendanceService attendanceService,
                                IStudentService studentService,
                                ISubjectService subjectService) {
        this.attendanceService = attendanceService;
        this.studentService = studentService;
        this.subjectService = subjectService;
    }

    // ── STAFF: GET /staff/attendance ──────────────────────────────────────────
    // Renders staff-attendance.html; pre-loads subjects for the dropdown
    @GetMapping("/staff/attendance")
    public String staffAttendancePage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("staff", staff);
        model.addAttribute("subjects", subjects);
        return "staff-attendance";
    }

    // ── STAFF: GET /staff/attendance/students ─────────────────────────────────
    // AJAX/form call: "Load Students" button sends subjectId, date, semester
    // Returns the attendance table section; re-renders staff-attendance.html with student list
    @GetMapping("/staff/attendance/load")
    public String loadStudents(@RequestParam Long subjectId,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               @RequestParam(required = false) String semester,
                               HttpSession session,
                               Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<Student> students;
        if (semester != null && !semester.isBlank()) {
            students = studentService.findBySemester(semester);
        } else {
            students = studentService.findAll();
        }

        // For each student, check if attendance was already marked today
        List<Attendance> existingRecords = attendanceService.findBySubjectAndDate(subjectId, date);

        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("staff", staff);
        model.addAttribute("subjects", subjects);
        model.addAttribute("students", students);
        model.addAttribute("existingRecords", existingRecords);
        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedSemester", semester);
        return "staff-attendance";
    }

    // ── STAFF: POST /staff/attendance/save ────────────────────────────────────
    // "💾 Save Attendance" button submits a list of studentId:status pairs
    @PostMapping("/staff/attendance/save")
    public String saveAttendance(@RequestParam Long subjectId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 @RequestParam List<Long> studentIds,
                                 @RequestParam List<String> statuses,
                                 HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        Subject subject = subjectService.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        for (int i = 0; i < studentIds.size(); i++) {
            Long studentId = studentIds.get(i);
            String status = statuses.get(i);

            Student student = studentService.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

            // Upsert: if already marked, skip to avoid duplicates
            if (!attendanceService.isAlreadyMarked(studentId, subjectId, date)) {
                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setSubject(subject);
                attendance.setMarkedBy(staff);
                attendance.setDate(date);
                attendance.setStatus(status);
                attendanceService.markAttendance(attendance);
            }
        }

        return "redirect:/staff/attendance?saved=true";
    }

    // ── STUDENT: GET /student/attendance ─────────────────────────────────────
    // Loads student-attendance.html with full attendance data for the logged-in student
    @GetMapping("/student/attendance")
    public String studentAttendancePage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        Long studentId = student.getId();

        // Recent attendance log (the bottom table in student-attendance.html)
        List<Attendance> recentLog = attendanceService.findByStudentNewestFirst(studentId);

        // Subject-wise attendance summary
        List<Subject> subjects = subjectService.findAll();
        List<SubjectAttendanceSummary> summaries = new ArrayList<>();
        long totalPresent = 0, totalClasses = 0;

        for (Subject subject : subjects) {
            long present = attendanceService.countPresent(studentId, subject.getId());
            long absent = attendanceService.countAbsent(studentId, subject.getId());
            long total = present + absent;
            double percentage = total == 0 ? 0 : (present * 100.0) / total;
            summaries.add(new SubjectAttendanceSummary(subject.getSubjectName(), total, present, absent, percentage));
            totalPresent += present;
            totalClasses += total;
        }

        double overallPct = totalClasses == 0 ? 0 : (totalPresent * 100.0) / totalClasses;

        model.addAttribute("student", student);
        model.addAttribute("summaries", summaries);
        model.addAttribute("recentLog", recentLog);
        model.addAttribute("overallPercentage", Math.round(overallPct));
        model.addAttribute("totalPresent", totalPresent);
        model.addAttribute("totalAbsent", totalClasses - totalPresent);
        model.addAttribute("totalClasses", totalClasses);
        return "student-attendance";
    }

    // ── Inner DTO for subject-wise summary passed to Thymeleaf ────────────────
    public static class SubjectAttendanceSummary {
        public final String subjectName;
        public final long total;
        public final long present;
        public final long absent;
        public final double percentage;

        public SubjectAttendanceSummary(String subjectName, long total, long present, long absent, double percentage) {
            this.subjectName = subjectName;
            this.total = total;
            this.present = present;
            this.absent = absent;
            this.percentage = percentage;
        }
    }
}