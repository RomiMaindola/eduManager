package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.ResultRepository;
import org.example.edumanager.Repositiory.StudentRepository;
import org.example.edumanager.Repositiory.SubjectRepository;
import org.example.edumanager.Service.IResultService;
import org.example.edumanager.entity.Result;
import org.example.edumanager.entity.Student;
import org.example.edumanager.entity.Subject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResultServiceImp implements IResultService {

    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public ResultServiceImp(ResultRepository resultRepository,
                            StudentRepository studentRepository,
                            SubjectRepository subjectRepository) {
        this.resultRepository = resultRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Result save(Result result) {
        return resultRepository.save(result);
    }

    @Override
    public Optional<Result> findById(Long id) {
        return resultRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        resultRepository.deleteById(id);
    }

    @Override
    public Optional<Result> findExisting(Long studentId, Long subjectId, String semester, String examType) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));
        return resultRepository.findByStudentAndSubjectAndSemesterAndExamType(student, subject, semester, examType);
    }

    @Override
    public boolean existsResult(Long studentId, Long subjectId, String semester, String examType) {
        return findExisting(studentId, subjectId, semester, examType).isPresent();
    }

    @Override
    public List<Result> findByStudent(Student student) {
        return resultRepository.findByStudent(student);
    }

    @Override
    public List<Result> findByStudentId(Long studentId) {
        return resultRepository.findByStudentId(studentId);
    }

    @Override
    public List<Result> findByStudentAndSemester(Long studentId, String semester) {
        return resultRepository.findByStudentIdAndSemester(studentId, semester);
    }

    @Override
    public List<Result> findBySubjectSemesterExamType(Long subjectId, String semester, String examType) {
        return resultRepository.findBySubjectIdAndSemesterAndExamType(subjectId, semester, examType);
    }

    @Override
    public List<Result> findBySubject(Subject subject) {
        return resultRepository.findBySubject(subject);
    }

    @Override
    public List<Result> findBySubjectId(Long subjectId) {
        return resultRepository.findBySubjectId(subjectId);
    }
}