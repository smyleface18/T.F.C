package com.startup.TFC.repositories;

import com.startup.TFC.entities.FinalProject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryFinalProject extends CrudRepository<FinalProject, Long> {
}
