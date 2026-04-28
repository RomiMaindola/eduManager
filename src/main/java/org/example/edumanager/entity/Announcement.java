package org.example.edumanager.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;


    private String content;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Staff createdBy;

    private LocalDateTime createdAt;

    // Templates show priority (Normal / Urgent / Important)
    private String priority;
}
