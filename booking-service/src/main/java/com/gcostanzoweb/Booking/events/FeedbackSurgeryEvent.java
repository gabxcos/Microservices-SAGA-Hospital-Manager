package com.gcostanzoweb.Booking.events;

import lombok.Data;

import com.gcostanzoweb.Booking.events.Event;

@Data
public class FeedbackSurgeryEvent implements Event {
    
    private static final String EVENT_TYPE = "FeedbackSurgeryEvent";

    private Integer surgeryId;
    private Integer patientId;
    private Integer bookingId;

    private Boolean triage;

    private String action; /* "BOOK" or "FREE" */

    private String result; /* "SUCCESS" or "FAILURE" */
    private String resultMessage;

    public FeedbackSurgeryEvent(){

    }

    public FeedbackSurgeryEvent(Integer surgeryId, Integer patientId, Integer bookingId, String action, Boolean success, Boolean triage){
        this.surgeryId = surgeryId;
        this.patientId = patientId;
        this.bookingId = bookingId;

        this.triage = triage;

        this.action = action;

        this.result = success ? "SUCCESS" : "FAILURE";

        if(result=="SUCCESS"){
            this.resultMessage = "Patient #" + this.patientId.toString() + " was successfully reserved for Booking #" + this.bookingId.toString() + ", for Surgery #" + this.surgeryId.toString() + ".";
        }else if(result=="FAILURE"){
            if(this.action=="FAIL_BOOKING") this.resultMessage = "No Booking #" + this.bookingId.toString() + " found.";
            else this.resultMessage = "Couldn't update reservation for Patient #" + this.patientId.toString() + " for Booking #" + this.bookingId.toString() + ", for Surgery #" + this.surgeryId.toString() + ".";
        }else this.resultMessage = "Unknown status for this event";
    }

}
