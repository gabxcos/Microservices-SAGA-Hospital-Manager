package com.gcostanzoweb.Booking.events;

import lombok.Data;


import com.gcostanzoweb.Booking.events.Event;

@Data
public class NotifySurgeryEvent implements Event {
    
    private static final String EVENT_TYPE = "NotifySurgeryEvent";

    private Boolean triage;

    private Integer surgeryId;
    private Integer patientId;
    private Integer bookingId;

    private String action; /* "BOOK" or "FREE" */

}
