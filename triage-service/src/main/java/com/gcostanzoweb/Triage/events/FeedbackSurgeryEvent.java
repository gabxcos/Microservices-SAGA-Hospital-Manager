package com.gcostanzoweb.Triage.events;

import lombok.Data;

import com.gcostanzoweb.Triage.events.Event;

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

}
