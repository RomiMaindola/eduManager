package org.example.edumanager.entity;
import jakarta.persistence.*;
import lombok.*;


    @Entity
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Student {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private String admissionNo;

        private String name;

        @Column(unique = true)
        private String email;

        private String password;

        private String dob;

        private String semester;

        private String branch;



        private String role = "ROLE_STUDENT";
    }

