package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.StudentRepository;
import org.example.edumanager.Service.IStudentService;
import org.example.edumanager.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImp implements IStudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImp(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student save(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> findBySemester(String semester) {
        return studentRepository.findBySemester(semester);
    }

    @Override
    public List<Student> findByBranch(String branch) {
        return studentRepository.findByBranch(branch);
    }

    @Override
    public List<Student> findBySemesterAndBranch(String semester, String branch) {
        return studentRepository.findBySemesterAndBranch(semester, branch);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Override
    public Optional<Student> findByAdmissionNo(String admissionNo) {
        return studentRepository.findByAdmissionNo(admissionNo);
    }

    @Override
    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByAdmissionNo(String admissionNo) {
        return studentRepository.existsByAdmissionNo(admissionNo);
    }
}