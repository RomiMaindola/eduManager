package org.example.edumanager.Service.Imp;

import org.example.edumanager.Repositiory.AnnouncementRepository;
import org.example.edumanager.Service.IAnnouncementService;
import org.example.edumanager.entity.Announcement;
import org.example.edumanager.entity.Staff;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementServiceImp implements IAnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementServiceImp(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @Override
    public Announcement save(Announcement announcement) {
        if (announcement.getCreatedAt() == null) {
            announcement.setCreatedAt(LocalDateTime.now());
        }
        // Default audience to "All Students" if not provided
        if (announcement.getAudience() == null || announcement.getAudience().isBlank()) {
            announcement.setAudience("All Students");
        }
        // Default priority to "Normal" if not provided
        if (announcement.getPriority() == null || announcement.getPriority().isBlank()) {
            announcement.setPriority("Normal");
        }
        return announcementRepository.save(announcement);
    }

    @Override
    public Optional<Announcement> findById(Long id) {
        return announcementRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public List<Announcement> findAllNewestFirst() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Returns announcements visible to a student in a given semester.
     * Includes "All Students" audience + their specific semester (e.g. "5th Semester").
     * Matches the audience dropdown values in staff-announcement.html.
     */
    @Override
    public List<Announcement> findForStudent(String studentSemester) {
        // Build audience list: global + semester-specific
        String semesterLabel = studentSemester + " Semester"; // e.g. "5th Semester"
        List<String> audiences = List.of("All Students", semesterLabel);
        return announcementRepository.findByAudienceInOrderByCreatedAtDesc(audiences);
    }

    @Override
    public List<Announcement> findByStaff(Staff staff) {
        return announcementRepository.findByCreatedBy(staff);
    }

    @Override
    public List<Announcement> findByStaffId(Long staffId) {
        return announcementRepository.findByCreatedById(staffId);
    }

    @Override
    public List<Announcement> findByStaffNewestFirst(Staff staff) {
        return announcementRepository.findByCreatedByOrderByCreatedAtDesc(staff);
    }

    @Override
    public List<Announcement> findByPriority(String priority) {
        return announcementRepository.findByPriorityOrderByCreatedAtDesc(priority);
    }
}