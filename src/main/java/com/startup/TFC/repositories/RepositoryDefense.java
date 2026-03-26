package com.startup.TFC.repositories;

import com.startup.TFC.entities.Defense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryDefense extends CrudRepository<Defense, Long> {
}
