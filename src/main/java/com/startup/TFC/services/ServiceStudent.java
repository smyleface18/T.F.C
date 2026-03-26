package com.startup.TFC.services;

import com.startup.TFC.entities.Student;
import com.startup.TFC.repositories.RepositoryStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<Student> findById(Long id) {
        return repo.findById(id);
    }

    public Student save(Student student) {
        return repo.save(student);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    public long count() {
        return repo.count();
    }
}
