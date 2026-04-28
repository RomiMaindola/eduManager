package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.StaffRepository;
import org.example.edumanager.Service.IStaffService;
import org.example.edumanager.entity.Staff;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImp implements IStaffService {

    private final StaffRepository staffRepository;

    public StaffServiceImp(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public Optional<Staff> findById(Long id) {
        return staffRepository.findById(id);
    }

    @Override
    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }

    @Override
    public List<Staff> findByDepartment(String department) {
        return staffRepository.findByDepartment(department);
    }

    @Override
    public Optional<Staff> findByEmail(String email) {
        return staffRepository.findByEmail(email);
    }

    @Override
    public Optional<Staff> findByStaffNo(String staffNo) {
        return staffRepository.findByStaffNo(staffNo);
    }

    @Override
    public boolean existsByEmail(String email) {
        return staffRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByStaffNo(String staffNo) {
        return staffRepository.existsByStaffNo(staffNo);
    }
}