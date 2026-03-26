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
@Table(name = "committees")
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
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