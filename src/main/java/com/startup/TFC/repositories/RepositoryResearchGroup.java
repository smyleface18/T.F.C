package com.startup.TFC.repositories;

import com.startup.TFC.entities.ResearchGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryResearchGroup extends CrudRepository<ResearchGroup, Long> {

    @Query("SELECT rg FROM ResearchGroup rg LEFT JOIN FETCH rg.students")
    List<ResearchGroup> findAllWithStudents();

}
