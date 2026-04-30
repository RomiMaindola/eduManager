package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.edumanager.Repositiory.SubjectRepository;
import org.example.edumanager.Service.INotesService;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Notes;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class NotesController {

    private final INotesService notesService;
    private final ISubjectService subjectService;
    private final SubjectRepository subjectRepository;

    // Files are stored under src/main/resources/static/uploads/notes/
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/notes/";

    public NotesController(INotesService notesService,
                           ISubjectService subjectService,
                           SubjectRepository subjectRepository) {
        this.notesService = notesService;
        this.subjectService = subjectService;
        this.subjectRepository = subjectRepository;
    }

    // ── STAFF: GET /staff/notes ───────────────────────────────────────────────
    @GetMapping("/staff/notes")
    public String staffNotesPage(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        List<Notes> myNotes = notesService.findByStaffId(staff.getId());
        List<Subject> subjects = subjectService.findAll();

        model.addAttribute("staff", staff);
        model.addAttribute("notesList", myNotes);
        model.addAttribute("subjects", subjects);
        return "staff-notes";
    }

    // ── STAFF: POST /staff/notes/upload ──────────────────────────────────────
    @PostMapping("/staff/notes/upload")
    public String uploadNotes(@RequestParam String title,
                              @RequestParam Long subjectId,
                              @RequestParam(required = false) String description,
                              @RequestParam(defaultValue = "Notes") String type,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session) throws IOException {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff staff)) return "redirect:/staff/login-page";

        Subject subject = subjectService.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        Files.copy(file.getInputStream(), uploadPath.resolve(uniqueFilename));

        String fileUrl = "/uploads/notes/" + uniqueFilename;

        Notes notes = new Notes();
        notes.setTitle(title);
        notes.setSubject(subject);
        notes.setUploadedBy(staff);
        notes.setDescription(description);
        notes.setType(type);
        notes.setFileUrl(fileUrl);
        notesService.save(notes);

        return "redirect:/staff/notes?uploaded=true";
    }

    // ── STAFF: POST /staff/notes/delete/{id} ─────────────────────────────────
    @PostMapping("/staff/notes/delete/{id}")
    public String deleteNote(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        notesService.deleteById(id);
        return "redirect:/staff/notes";
    }

    // ── STUDENT: GET /student/notes ───────────────────────────────────────────
    // KEY CHANGE: subjects list is filtered to the student's semester only.
    // notesList is also scoped to that semester (optionally narrowed by subjectId).
    @GetMapping("/student/notes")
    public String studentNotesPage(@RequestParam(required = false) Long subjectId,
                                   HttpSession session,
                                   Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        // Only subjects that belong to this student's semester
        List<Subject> semesterSubjects = subjectRepository.findBySemester(student.getSemester());

        List<Notes> notesList;
        if (subjectId != null) {
            final Long tempSubjectId = subjectId;
            // Extra safety: ensure the requested subjectId belongs to the student's semester
            boolean belongsToSemester = semesterSubjects.stream()
                    .anyMatch(s -> s.getId().equals(tempSubjectId));
            if (belongsToSemester) {
                notesList = notesService.findBySubjectIdNewestFirst(subjectId);
            } else {
                // Ignore invalid/cross-semester filter — show all semester notes
                notesList = notesForSemester(semesterSubjects);
                subjectId = null; // reset so the dropdown shows "All Subjects"
            }
        } else {
            // Fetch notes for all subjects in this semester
            notesList = notesForSemester(semesterSubjects);
        }

        model.addAttribute("student", student);
        model.addAttribute("notesList", notesList);
        model.addAttribute("subjects", semesterSubjects);      // semester-scoped subjects only
        model.addAttribute("selectedSubjectId", subjectId);
        return "student-notes";
    }

    /**
     * Collects and returns notes for all subjects in the given list,
     * ordered newest first per subject.
     */
    private List<Notes> notesForSemester(List<Subject> subjects) {
        return subjects.stream()
                .flatMap(s -> notesService.findBySubjectIdNewestFirst(s.getId()).stream())
                .toList();
    }
}