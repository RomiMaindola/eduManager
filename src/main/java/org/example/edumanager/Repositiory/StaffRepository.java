package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff,Long> {
}
