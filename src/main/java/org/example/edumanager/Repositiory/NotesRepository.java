package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Notes;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes, Long> {

    // Student view: all notes for a subject (student-notes.html filter by subject)
    List<Notes> findBySubject(Subject subject);
    List<Notes> findBySubjectId(Long subjectId);

    // Student view: filter by type (Notes / Assignment)
    List<Notes> findByType(String type);
    List<Notes> findBySubjectAndType(Subject subject, String type);

    // Staff view: notes uploaded by a specific staff member (staff-notes.html table)
    List<Notes> findByUploadedBy(Staff uploadedBy);
    List<Notes> findByUploadedById(Long staffId);

    // Staff view: their notes for a specific subject
    List<Notes> findByUploadedByAndSubject(Staff uploadedBy, Subject subject);
    List<Notes> findByUploadedByIdAndSubjectId(Long staffId, Long subjectId);

    // All notes ordered newest first (default listing)
    List<Notes> findAllByOrderByIdDesc();
    List<Notes> findBySubjectIdOrderByIdDesc(Long subjectId);
}