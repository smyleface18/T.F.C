package com.startup.TFC.services;

import com.startup.TFC.entities.Professor;
import com.startup.TFC.entities.Student;
import com.startup.TFC.repositories.RepositoryProfessor;
import com.startup.TFC.repositories.RepositoryStudent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceProfessor {

    private final RepositoryProfessor repo;
    private final RepositoryStudent repoStudent;

    @Autowired
    public ServiceProfessor(RepositoryProfessor repo, RepositoryStudent repoStudent) {
        this.repo = repo;
        this.repoStudent = repoStudent;
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

    @Transactional(readOnly = true)
    public List<String> getHelpedStudentNames(String professorDni) {
        Professor professor = this.repo.findById(professorDni)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        // Convertir la lista de estudiantes a nombre + id
        return professor.getHelpedStudents().stream()
                .map(s -> s.getName() + ", id: " + s.getStudentId())
                .toList();
    }

    @Transactional(readOnly = true)
    public Iterable<Student> getAllStudents() {
        return this.repoStudent.findAll(); // necesitas inyectar RepositoryStudent
    }
}
