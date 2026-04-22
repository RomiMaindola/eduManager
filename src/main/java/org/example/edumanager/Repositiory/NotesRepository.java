package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotesRepository extends JpaRepository<Notes,Long> {
}
