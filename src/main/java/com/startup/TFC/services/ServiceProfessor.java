package com.startup.TFC.services;

import com.startup.TFC.entities.Professor;
import com.startup.TFC.repositories.RepositoryProfessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceProfessor {

    private final RepositoryProfessor repo;

    @Autowired
    public ServiceProfessor(RepositoryProfessor repo) {
        this.repo = repo;
    }

    public Iterable<Professor> findAll() {
        return repo.findAll();
    }

    public Optional<Professor> findById(String dni) {
        return repo.findById(dni);
    }

    public Professor save(Professor professor) {
        return repo.save(professor);
    }

    public void deleteById(String dni) {
        repo.deleteById(dni);
    }

    public boolean existsById(String dni) {
        return repo.existsById(dni);
    }

    public long count() {
        return repo.count();
    }
}
