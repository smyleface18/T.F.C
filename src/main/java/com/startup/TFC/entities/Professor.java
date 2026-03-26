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

    @Column
    private String name;

    @Column
    private String address;

    @Column(nullable = false)
    private String area;


    // Many-to-Many with Committee
    @ManyToMany(mappedBy = "professors")
    private List<Committee> committees;

    @OneToMany(mappedBy = "director")
    private List<Student> supervisedStudents;

    @ManyToMany(mappedBy = "helpers")
    private List<Student> helpedStudents;
}