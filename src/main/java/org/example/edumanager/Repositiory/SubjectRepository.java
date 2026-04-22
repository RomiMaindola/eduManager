package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
}
