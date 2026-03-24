package com.startup.TFC.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "research_groups")
public class ResearchGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer memberCount;

    @OneToMany(mappedBy = "researchGroup")
    private List<Student> students;
}