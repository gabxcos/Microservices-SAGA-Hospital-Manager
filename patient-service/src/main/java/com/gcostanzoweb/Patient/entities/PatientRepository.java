package com.gcostanzoweb.Patient.entities;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@Repository
public interface PatientRepository extends CrudRepository<Patient, Integer>{
    
}
