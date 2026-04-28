package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.NotesRepository;
import org.example.edumanager.Repositiory.SubjectRepository;
import org.example.edumanager.Service.INotesService;
import org.example.edumanager.entity.Notes;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Subject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotesServiceImp implements INotesService {

    private final NotesRepository notesRepository;
    private final SubjectRepository subjectRepository;

    public NotesServiceImp(NotesRepository notesRepository,
                           SubjectRepository subjectRepository) {
        this.notesRepository = notesRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Notes save(Notes notes) {
        return notesRepository.save(notes);
    }

    @Override
    public Optional<Notes> findById(Long id) {
        return notesRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        notesRepository.deleteById(id);
    }

    @Override
    public List<Notes> findBySubject(Subject subject) {
        return notesRepository.findBySubject(subject);
    }

    @Override
    public List<Notes> findBySubjectId(Long subjectId) {
        return notesRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Notes> findBySubjectIdNewestFirst(Long subjectId) {
        return notesRepository.findBySubjectIdOrderByIdDesc(subjectId);
    }

    @Override
    public List<Notes> findByType(String type) {
        return notesRepository.findByType(type);
    }

    @Override
    public List<Notes> findBySubjectAndType(Long subjectId, String type) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));
        return notesRepository.findBySubjectAndType(subject, type);
    }

    @Override
    public List<Notes> findByStaff(Staff staff) {
        return notesRepository.findByUploadedBy(staff);
    }

    @Override
    public List<Notes> findByStaffId(Long staffId) {
        return notesRepository.findByUploadedById(staffId);
    }

    @Override
    public List<Notes> findByStaffAndSubject(Long staffId, Long subjectId) {
        return notesRepository.findByUploadedByIdAndSubjectId(staffId, subjectId);
    }

    @Override
    public List<Notes> findAllNewestFirst() {
        return notesRepository.findAllByOrderByIdDesc();
    }
}