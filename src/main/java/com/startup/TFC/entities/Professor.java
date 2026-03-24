package com.startup.TFC.entities;

import com.startup.TFC.entities.Committee;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "professors")
public class Professor {

    @Id
    private String dni;

    private String name;
    private String address;

    // Professor directs students
    @OneToMany
    @JoinColumn(name = "director_dni")
    private List<Student> supervisedStudents;

    // Many-to-Many with Committee
    @ManyToMany(mappedBy = "professors")
    private List<Committee> committees;

    // Professors who help students
    @ManyToMany(mappedBy = "helpingProfessors")
    private List<FinalProject> assistedProject;
}