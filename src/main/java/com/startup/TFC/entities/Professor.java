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

    // Alumno que dirige
    @OneToOne(mappedBy = "director", fetch = FetchType.EAGER)
    private Student supervisedStudent;

    // Alumnos a los que ayuda (sin dirigir)
    @ManyToMany(mappedBy = "helpers", fetch = FetchType.EAGER)
    private List<Student> helpedStudents;
}