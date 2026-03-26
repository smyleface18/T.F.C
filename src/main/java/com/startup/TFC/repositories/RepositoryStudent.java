package com.startup.TFC.repositories;

import com.startup.TFC.entities.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryStudent extends CrudRepository<Student, Long> {
}
