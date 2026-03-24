package com.startup.TFC.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "committees")
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examLocation;
    private Integer memberCount;

    // Many-to-Many with Professors
    @ManyToMany
    @JoinTable(
        name = "committee_professor",
        joinColumns = @JoinColumn(name = "committee_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private List<Professor> professors;

    // One Committee has many defenses
    @OneToMany(mappedBy = "committee")
    private List<Defense> defenses;
}