package com.gcostanzoweb.Booking.service;

import com.gcostanzoweb.Booking.entities.BookingRepository;
import com.gcostanzoweb.Booking.entities.Booking;
import com.gcostanzoweb.Booking.events.FeedbackSurgeryEvent;
import com.gcostanzoweb.Booking.events.NotifySurgeryEvent;
import com.gcostanzoweb.Booking.entities.Booking;
import com.gcostanzoweb.Booking.stream.SagaStream;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MainService {
    
    @Autowired
    private BookingRepository bookingRepository;

    private final SagaStream stream;

    public MainService(SagaStream stream) {
        this.stream = stream;
    }

    // INITIALIZATION
    @PostConstruct
    public void initSurgeries() {
        bookingRepository.saveAll(
            Stream.of(
                new Booking("Pronto Soccorso", 8),
                new Booking("Reparto 1", 3),
                new Booking("Reparto 2", 4)
            ).collect(Collectors.toList()));
    }

    // MESSAGE HANDLING
    @StreamListener(SagaStream.INPUT)
    public void handleInboundEvent(@Payload NotifySurgeryEvent ev){
        System.out.println("Responding to event: " + ev.toString());

        Optional<Booking> ob = bookingRepository.findById(ev.getBookingId());
        if(ob.isPresent()){
            Booking b = ob.get();

            if(b.getBusy() < b.getMaxBusy() && ev.getAction()=="BOOK"){
                b.setBusy(b.getBusy()+1);
                this.handleOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "BOOK", true, ev.getTriage());
            }else if(b.getBusy()>0 && ev.getAction()=="FREE"){
                b.setBusy(b.getBusy()-1);
                this.handleOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FREE", true, ev.getTriage());
            }else{
                this.handleOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getAction(), false, ev.getTriage());
            }

            b = bookingRepository.save(b);
        }else{
            this.handleOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_BOOKING", false, ev.getTriage());
        }
    }

    @SendTo(SagaStream.OUTPUT)
    public void handleOutboundEvent(Integer surgeryId, Integer patientId, Integer bookingId, String action, Boolean success, Boolean triage){
        FeedbackSurgeryEvent ev = new FeedbackSurgeryEvent(surgeryId, patientId, bookingId, action, success, triage);
        System.out.println("Sending out event: " + ev.toString());
        this.stream.outboundMessages().send(MessageBuilder.withPayload(ev).build());
    }
}
