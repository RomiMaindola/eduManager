package org.example.edumanager.Service.Imp;

import org.example.edumanager.Dto.LoginRequest;
import org.example.edumanager.Repositiory.StaffRepository;
import org.example.edumanager.Repositiory.StudentRepository;
import org.example.edumanager.entity.Staff;
import org.example.edumanager.entity.Student;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final StudentRepository studentRepo;
    private final StaffRepository staffRepo;
    private final BCryptPasswordEncoder encoder;

    public AuthService(StudentRepository studentRepo,
                       StaffRepository staffRepo,
                       BCryptPasswordEncoder encoder) {
        this.studentRepo = studentRepo;
        this.staffRepo = staffRepo;
        this.encoder = encoder;
    }

    /**
     * Returns authenticated Student or Staff, or null if credentials are wrong.
     */
    public Object authenticateUser(LoginRequest request) {

        // STUDENT login: needs email + admissionNo + password
        if (request.getAdmissionNo() != null && !request.getAdmissionNo().isBlank()) {
            Optional<Student> opt = studentRepo
                    .findByEmailAndAdmissionNo(request.getEmail(), request.getAdmissionNo());
            if (opt.isPresent() && encoder.matches(request.getPassword(), opt.get().getPassword())) {
                return opt.get();
            }
        }

        // STAFF login: needs email + staffNo + password
        if (request.getStaffNo() != null && !request.getStaffNo().isBlank()) {
            Optional<Staff> opt = staffRepo
                    .findByEmailAndStaffNo(request.getEmail(), request.getStaffNo());
            if (opt.isPresent() && encoder.matches(request.getPassword(), opt.get().getPassword())) {
                return opt.get();
            }
        }

        return null;
    }
}
