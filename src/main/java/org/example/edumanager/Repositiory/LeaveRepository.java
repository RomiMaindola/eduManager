package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<LeaveApplication,Long> {
}
