package com.gcostanzoweb.Surgery.service;

import com.gcostanzoweb.Surgery.events.FeedbackSurgeryEvent;
import com.gcostanzoweb.Surgery.events.NotifySurgeryEvent;
import com.gcostanzoweb.Surgery.stream.SagaStream;
import com.gcostanzoweb.Surgery.entities.SurgeryRepository;
import com.gcostanzoweb.Surgery.entities.Surgery;

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
                // Reparto
                new Surgery(11, 2, 1), // Reparto 1
                new Surgery(12, 2, 2),
                new Surgery(13, 2, 1),
                new Surgery(14, 2, 2),
                new Surgery(15, 2, 3),
                new Surgery(16, 3, 1), // Reparto 2
                new Surgery(17, 3, 3),
                new Surgery(18, 3, 1),
                new Surgery(19, 3, 3),
                new Surgery(20, 3, 2)
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
    public void pushSurgeryQueue(){
        List<Surgery> ls = surgeryRepository.findByStatusOrderByDateAscIdAsc("WAITING");
        if(!ls.isEmpty()){
            Surgery s = ls.get(0);
            s.setStatus("PROCESSING");

            s = surgeryRepository.save(s);

            this.handleOutboundEvent(s.getId(), s.getIdPatient(), s.getIdBooking(), "BOOK");

        }else System.out.println("No patients waiting in pre-surgery.");
    }
}
