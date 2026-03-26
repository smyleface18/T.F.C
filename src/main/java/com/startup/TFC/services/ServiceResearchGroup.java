package com.startup.TFC.services;

import com.startup.TFC.entities.ResearchGroup;
import com.startup.TFC.repositories.RepositoryResearchGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ServiceResearchGroup {
    private final RepositoryResearchGroup repo;
    @Autowired
    public ServiceResearchGroup(RepositoryResearchGroup repo) { this.repo = repo; }

    public Iterable<ResearchGroup> findAll() { return repo.findAllWithStudents(); }
    public Optional<ResearchGroup> findById(Long id) { return repo.findById(id); }
    public ResearchGroup save(ResearchGroup g) { return repo.save(g); }
    public void deleteById(Long id) { repo.deleteById(id); }
    public boolean existsById(Long id) { return repo.existsById(id); }
    public long count() { return repo.count(); }
}
