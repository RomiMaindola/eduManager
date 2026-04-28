package org.example.edumanager.Repositiory;

import org.example.edumanager.entity.Announcement;
import org.example.edumanager.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // Student view: all announcements newest-first (student-announcement.html)
    List<Announcement> findAllByOrderByCreatedAtDesc();

//    // Student view: filter by audience ("All Students" or their semester e.g. "5th Semester")
//    List<Announcement> findByAudienceOrderByCreatedAtDesc(String audience);
//
//    // Fetch for a student: global + their semester (use @Query or two calls in service)
//    List<Announcement> findByAudienceInOrderByCreatedAtDesc(List<String> audiences);

    // Staff view: announcements posted by a specific staff member
    List<Announcement> findByCreatedBy(Staff createdBy);
    List<Announcement> findByCreatedById(Long staffId);
    List<Announcement> findByCreatedByOrderByCreatedAtDesc(Staff createdBy);

    // Filter by priority (Urgent / Important / Normal)
    List<Announcement> findByPriorityOrderByCreatedAtDesc(String priority);
}