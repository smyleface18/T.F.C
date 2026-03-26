package com.startup.TFC.services;

import com.startup.TFC.entities.FinalProject;
import com.startup.TFC.repositories.RepositoryFinalProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ServiceFinalProject {
    private final RepositoryFinalProject repo;
    @Autowired
    public ServiceFinalProject(RepositoryFinalProject repo) { this.repo = repo; }
    public Iterable<FinalProject> findAll() { return repo.findAll(); }
    public Optional<FinalProject> findById(Long id) { return repo.findById(id); }
    public FinalProject save(FinalProject fp) { return repo.save(fp); }
    public void deleteById(Long id) { repo.deleteById(id); }
    public boolean existsById(Long id) { return repo.existsById(id); }
    public long count() { return repo.count(); }
}
