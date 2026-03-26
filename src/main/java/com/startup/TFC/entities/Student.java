package com.startup.TFC.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId; // matricula

    @Column(nullable = false)
    private String dni;

    @Column(nullable = false)
    private String name;

    @OneToOne(mappedBy = "student")
    private FinalProject finalProject;

    @OneToOne
    @JoinColumn(name = "director_dni")
    private Professor director;

    @ManyToMany
    @JoinTable(
            name = "student_professor_help",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "profesor_dni")
    )
    private List<Professor> helpers;

    @ManyToOne
    @JoinColumn(name = "research_group_id")
    private ResearchGroup researchGroup;


    private Date groupJoinDate;
}