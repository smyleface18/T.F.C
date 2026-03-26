package com.startup.TFC.services;

import com.startup.TFC.entities.Defense;
import com.startup.TFC.repositories.RepositoryDefense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ServiceDefense {
    private final RepositoryDefense repo;
    @Autowired
    public ServiceDefense(RepositoryDefense repo) { this.repo = repo; }
    public Iterable<Defense> findAll() { return repo.findAll(); }
    public Optional<Defense> findById(Long id) { return repo.findById(id); }
    public Defense save(Defense d) { return repo.save(d); }
    public void deleteById(Long id) { repo.deleteById(id); }
    public boolean existsById(Long id) { return repo.existsById(id); }
    public long count() { return repo.count(); }
}
