package com.gcostanzoweb.Triage.entities;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@Repository
public interface SurgeryRepository extends CrudRepository<Surgery, Integer> {
    public List<Surgery> findByStatusOrderByPriorityDescIdAsc(String status);
}
