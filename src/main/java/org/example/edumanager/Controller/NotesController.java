package org.example.edumanager.Controller;

import jakarta.servlet.http.HttpSession;
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

    // Files are stored under src/main/resources/static/uploads/notes/
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/notes/";

    public NotesController(INotesService notesService, ISubjectService subjectService) {
        this.notesService = notesService;
        this.subjectService = subjectService;
    }

    // ── STAFF: GET /staff/notes ───────────────────────────────────────────────
    // Renders staff-notes.html with the upload form and the "Uploaded Notes" table
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
    // "📤 Upload Notes" button submits the form in staff-notes.html
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

        // Save uploaded file to static folder so Thymeleaf can link to it
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
    // The 🗑️ delete button in the staff-notes.html table
    @PostMapping("/staff/notes/delete/{id}")
    public String deleteNote(@PathVariable Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Staff)) return "redirect:/staff/login-page";
        notesService.deleteById(id);
        return "redirect:/staff/notes";
    }

    // ── STUDENT: GET /student/notes ───────────────────────────────────────────
    // Renders student-notes.html with all notes (filter by subject if provided)
    @GetMapping("/student/notes")
    public String studentNotesPage(@RequestParam(required = false) Long subjectId,
                                   HttpSession session,
                                   Model model) {
        Object user = session.getAttribute("user");
        if (!(user instanceof Student student)) return "redirect:/student/login-page";

        List<Notes> notesList;
        if (subjectId != null) {
            notesList = notesService.findBySubjectIdNewestFirst(subjectId);
        } else {
            notesList = notesService.findAllNewestFirst();
        }

        List<Subject> subjects = subjectService.findAll();
        model.addAttribute("student", student);
        model.addAttribute("notesList", notesList);
        model.addAttribute("subjects", subjects);
        model.addAttribute("selectedSubjectId", subjectId);
        return "student-notes";
    }
}