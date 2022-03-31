package com.gcostanzoweb.Triage.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class Surgery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private Integer idPatient;
    private Integer idBooking;
    private String status; /* "WAITING", "PROCESSING", "RESERVED", "FREED", "FAILED" */
    private Integer priority;
    private Integer date; /* simulates a Date as a 1-365 range Integer */

    public Surgery(){
        
    }

    public Surgery(Integer idPatient, Integer idBooking, Integer priority){
        this.idPatient = idPatient;
        this.idBooking = idBooking;
        this.status = "WAITING";
        this.priority = priority;
        this.date = 0;
    }
}
