package com.startup.TFC.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "final_projects")
public class FinalProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column
    @Temporal(TemporalType.DATE)
    private Date initDate;

    // Relación 1:1 con Student - un TFC solo lo realiza un alumno
    @OneToOne
    @JoinColumn(name = "student_id", unique = true)
    private Student student;
}