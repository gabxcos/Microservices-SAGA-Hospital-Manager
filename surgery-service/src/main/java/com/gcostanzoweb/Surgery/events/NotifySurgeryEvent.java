package com.gcostanzoweb.Surgery.events;

import lombok.Data;

import com.gcostanzoweb.Surgery.events.Event;

@Data
public class NotifySurgeryEvent implements Event {
    
    private static final String EVENT_TYPE = "NotifySurgeryEvent";

    private Boolean triage;

    private Integer surgeryId;
    private Integer patientId;
    private Integer bookingId;

    private String action; /* "BOOK" or "FREE" */

    public NotifySurgeryEvent(){
        
    }

    public NotifySurgeryEvent(Integer surgeryId, Integer patientId, Integer bookingId, String action){
        this.surgeryId = surgeryId;
        this.patientId = patientId;
        this.bookingId = bookingId;

        this.triage = false;

        this.action = action;
    }

}
