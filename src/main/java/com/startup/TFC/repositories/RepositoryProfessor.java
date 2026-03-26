package com.startup.TFC.repositories;

import com.startup.TFC.entities.Professor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryProfessor extends CrudRepository<Professor, String> {
}
