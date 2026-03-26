package com.startup.TFC.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "defenses")
public class Defense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Temporal(TemporalType.DATE)
    private Date defenseDate;

    @ManyToOne
    @JoinColumn(name = "committee_id")
    private Committee committee;

    // Qué alumno se presenta
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Con qué TFC
    @ManyToOne
    @JoinColumn(name = "project_id")
    private FinalProject finalProject;
}