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