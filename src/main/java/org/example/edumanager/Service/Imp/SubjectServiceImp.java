package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.SubjectRepository;
import org.example.edumanager.Service.ISubjectService;
import org.example.edumanager.entity.Subject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImp implements ISubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImp(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public Optional<Subject> findById(Long id) {
        return subjectRepository.findById(id);
    }

    @Override
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        subjectRepository.deleteById(id);
    }

    @Override
    public List<Subject> findBySemester(String semester) {
        return subjectRepository.findBySemester(semester);
    }

    @Override
    public Optional<Subject> findBySubjectName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName);
    }

    @Override
    public Optional<Subject> findBySubjectNameAndSemester(String subjectName, String semester) {
        return subjectRepository.findBySubjectNameAndSemester(subjectName, semester);
    }

    @Override
    public boolean existsBySubjectNameAndSemester(String subjectName, String semester) {
        return subjectRepository.existsBySubjectNameAndSemester(subjectName, semester);
    }
}