package com.startup.TFC.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "professors")
public class Professor {

    @Id
    private String dni;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(nullable = false)
    private String area;

    @ManyToMany(mappedBy = "professors")
    private List<Committee> committees;

    // Alumnos que dirige
    @OneToMany(mappedBy = "director")
    private List<Student> supervisedStudents;

    // Alumnos a los que ayuda (sin dirigir)
    @ManyToMany(mappedBy = "helpers")
    private List<Student> helpedStudents;
}