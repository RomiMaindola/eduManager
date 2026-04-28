package org.example.edumanager.Service;

import org.example.edumanager.entity.Announcement;
import org.example.edumanager.entity.Staff;

import java.util.List;
import java.util.Optional;

public interface IAnnouncementService {

    // CRUD
    Announcement save(Announcement announcement);
    Optional<Announcement> findById(Long id);
    void deleteById(Long id);

    // Student view: all announcements newest-first
    List<Announcement> findAllNewestFirst();

    // Staff view: their own announcements
    List<Announcement> findByStaff(Staff staff);
    List<Announcement> findByStaffId(Long staffId);
    List<Announcement> findByStaffNewestFirst(Staff staff);

    // Filter by priority
    List<Announcement> findByPriority(String priority);
}