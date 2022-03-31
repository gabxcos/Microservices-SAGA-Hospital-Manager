package com.gcostanzoweb.Triage.service;

import com.gcostanzoweb.Triage.events.FeedbackSurgeryEvent;
import com.gcostanzoweb.Triage.events.NotifySurgeryEvent;
import com.gcostanzoweb.Triage.stream.SagaStream;
import com.gcostanzoweb.Triage.entities.SurgeryRepository;
import com.gcostanzoweb.Triage.entities.Surgery;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@EnableScheduling
public class MainService {

    @Autowired
    private SurgeryRepository surgeryRepository;
    
    private final SagaStream stream;

    public MainService(SagaStream stream) {
        this.stream = stream;
    }

    // INITIALIZATION
    @PostConstruct
    public void initSurgeries() {
        surgeryRepository.saveAll(
            Stream.of(
                // Triage
                new Surgery(1, 1, 1),
                new Surgery(2, 1, 1),
                new Surgery(3, 1, 1),
                new Surgery(4, 1, 2),
                new Surgery(5, 1, 2),
                new Surgery(6, 1, 2),
                new Surgery(7, 1, 3),
                new Surgery(8, 1, 3),
                new Surgery(9, 1, 3),
                new Surgery(10, 1, 4)
            ).collect(Collectors.toList()));
    }

    // MESSAGE HANDLING
    @StreamListener(SagaStream.INPUT)
    public void handleInboundEvent(@Payload FeedbackSurgeryEvent ev){
        System.out.println("Responding to event: " + ev.toString());

        Optional<Surgery> os = surgeryRepository.findById(ev.getSurgeryId());
        if(os.isPresent()){
            Surgery s = os.get();
            if(ev.getResult()=="SUCCESS"){
                if(ev.getAction()=="BOOK"){
                    s.setStatus("RESERVED");
                }else if(ev.getAction()=="FREE"){
                    s.setStatus("FREED");
                }
            }else if(ev.getResult()=="FAILURE"){
                if(ev.getAction()=="BOOK" || ev.getAction()=="FREE"){
                    s.setStatus("WAITING");
                }else if(ev.getAction()=="FAIL_PATIENT" || ev.getAction()=="FAIL_PATIENT_STATUS" || ev.getAction()=="FAIL_BOOKING"){
                    s.setStatus("FAILED");
                }
            }
            System.out.println(ev.getResultMessage());
            s = surgeryRepository.save(s);
        }else System.out.println("No surgery found with id "+ ev.getSurgeryId().toString());

    }

    @SendTo(SagaStream.OUTPUT)
    public void handleOutboundEvent(Integer surgeryId, Integer patientId, Integer bookingId, String action){
        NotifySurgeryEvent ev =  new NotifySurgeryEvent(surgeryId, patientId, bookingId, action);
        System.out.println("Sending out event: " + ev.toString());
        this.stream.outboundMessages().send(MessageBuilder.withPayload(ev).build());
    }

    // SCHEDULED BUSINESS LOGIC
    @Scheduled(fixedDelay = 20000)
    public void pushTriageQueue(){
        List<Surgery> ls = surgeryRepository.findByStatusOrderByPriorityDescIdAsc("WAITING");
        if(!ls.isEmpty()){
            Surgery s = ls.get(0);
            s.setStatus("PROCESSING");

            s = surgeryRepository.save(s);

            this.handleOutboundEvent(s.getId(), s.getIdPatient(), s.getIdBooking(), "BOOK");

        }else System.out.println("No patients waiting in triage.");
    }
}