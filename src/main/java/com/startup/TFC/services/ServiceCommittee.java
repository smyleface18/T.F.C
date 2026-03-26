package com.startup.TFC.services;

import com.startup.TFC.entities.Committee;
import com.startup.TFC.repositories.RepositoryCommittee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ServiceCommittee {
    private final RepositoryCommittee repo;
    @Autowired
    public ServiceCommittee(RepositoryCommittee repo) { this.repo = repo; }
    public Iterable<Committee> findAll() { return repo.findAllWithProfessors(); }
    public Optional<Committee> findById(Long id) { return repo.findById(id); }
    public Committee save(Committee c) { return repo.save(c); }
    public void deleteById(Long id) { repo.deleteById(id); }
    public boolean existsById(Long id) { return repo.existsById(id); }
    public long count() { return repo.count(); }
}
