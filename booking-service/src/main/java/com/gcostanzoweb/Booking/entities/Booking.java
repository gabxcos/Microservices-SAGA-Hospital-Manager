package com.gcostanzoweb.Booking.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;
    private Integer busy;
    private Integer maxBusy;

    public Booking(){
        
    }

    public Booking(String name, Integer maxBusy){
        this.name = name;
        this.busy = 0;
        this.maxBusy = maxBusy;
    }

}
