package com.gcostanzoweb.Patient.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer idSurgery;
    private String name;
    private String email;
    private String status; /* "SURGERY", "TRIAGE", "FREE" */

    public Patient(){
        
    }

    public Patient(String name, String email){
        this.idSurgery = -1;
        this.name = name;
        this.email = email;
        this.status = "FREE";
    }
}
