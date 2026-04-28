package org.example.edumanager.Service;

import org.example.edumanager.entity.Notes;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Subject;

import java.util.List;
import java.util.Optional;

public interface INotesService {

    // CRUD
    Notes save(Notes notes);
    Optional<Notes> findById(Long id);
    void deleteById(Long id);

    // Student view: all notes for a subject
    List<Notes> findBySubject(Subject subject);
    List<Notes> findBySubjectId(Long subjectId);
    List<Notes> findBySubjectIdNewestFirst(Long subjectId);

    // Student view: filter by type (Notes / Assignment)
    List<Notes> findByType(String type);
    List<Notes> findBySubjectAndType(Long subjectId, String type);

    // Staff view: notes uploaded by a specific staff member
    List<Notes> findByStaff(Staff staff);
    List<Notes> findByStaffId(Long staffId);
    List<Notes> findByStaffAndSubject(Long staffId, Long subjectId);

    // All notes newest-first (default listing)
    List<Notes> findAllNewestFirst();
}