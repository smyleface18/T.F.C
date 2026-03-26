package com.startup.TFC.repositories;

import com.startup.TFC.entities.Committee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryCommittee extends CrudRepository<Committee, Long> {

    @Query("SELECT c FROM Committee c LEFT JOIN FETCH c.professors")
    List<Committee> findAllWithProfessors();
}
