package com.startup.TFC.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private String studentId; // matricula

    private String dni;
    private String name;

}