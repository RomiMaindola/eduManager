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

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;       // non-null when applicant is a Student

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;           // non-null when applicant is a Staff member

    private String role;

    private String leaveType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    private String status; // Pending, Approved, Rejected
}
