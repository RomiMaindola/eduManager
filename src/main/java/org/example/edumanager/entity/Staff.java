package org.example.edumanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String staffNo;

    @Column(unique = true)
    private String email;

    private String password;

    private String department;

    private String role = "ROLE_STAFF";
}
