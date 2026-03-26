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

    @Column(nullable = false)
    private String examLocation;

    @Column
    private Integer memberCount;

    @ManyToMany
    @JoinTable(
            name = "committee_professor",
            joinColumns = @JoinColumn(name = "committee_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_dni")
    )
    private List<Professor> professors;

    @OneToMany(mappedBy = "committee")
    private List<Defense> defenses;
}