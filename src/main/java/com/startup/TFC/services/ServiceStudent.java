package com.startup.TFC.services;

import com.startup.TFC.entities.Student;
import com.startup.TFC.repositories.RepositoryStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceStudent {

    private final RepositoryStudent repo;

    @Autowired
    public ServiceStudent(RepositoryStudent repo) {
        this.repo = repo;
    }

    public Iterable<Student> findAll() {
        return repo.findAll();
    }
}
