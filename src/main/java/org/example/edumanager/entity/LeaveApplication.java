package org.example.edumanager.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String role;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    private String status; // Pending, Approved, Rejected
}
