package com.gcostanzoweb.Patient.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.gcostanzoweb.Patient.entities.PatientRepository;
import com.gcostanzoweb.Patient.events.FeedbackSurgeryEvent;
import com.gcostanzoweb.Patient.events.NotifySurgeryEvent;
import com.gcostanzoweb.Patient.stream.SagaStream;
import com.gcostanzoweb.Patient.entities.PatientRepository;
import com.gcostanzoweb.Patient.entities.Patient;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    @Autowired
    private PatientRepository patientRepository;
    
    private final SagaStream stream;

    public MainService(SagaStream stream) {
        this.stream = stream;
    }

    // INITIALIZATION
    @PostConstruct
    public void initSurgeries() {
        patientRepository.saveAll(
            Stream.of(
                // These names and emails are generated with: https://www.fakenamegenerator.com/
                new Patient("Mario Rossi", "mariorossi@mail.com"), //1
                new Patient("Maria Pia Siciliani", "MariaPiaSiciliani@jourrapide.com"),
                new Patient("Amina Davide", "AminaDavide@dayrep.com"),
                new Patient("Dalia Pagnotto", "DaliaPagnotto@jourrapide.com"),
                new Patient("Agnese Lucciano", "AgneseLucciano@dayrep.com"), //5
                new Patient("Isabella Lo Duca", "IsabellaLoDuca@teleworm.us"),
                new Patient("Giacinta Marcelo", "GiacintaMarcelo@teleworm.us"),
                new Patient("Vladimiro Sal", "VladimiroSal@rhyta.com"),
                new Patient("Claudia Genovesi", "ClaudiaGenovesi@rhyta.com"),
                new Patient("Liviana Romano", "LivianaRomano@dayrep.com"), //10
                new Patient("Tranquillina Buccho", "TranquillinaBuccho@dayrep.com"),
                new Patient("Abelino Pugliesi", "AbelinoPugliesi@teleworm.us"),
                new Patient("Oberto Loggia", "ObertoLoggia@dayrep.com"),
                new Patient("Alessandra Angelo", "AlessandraAngelo@armyspy.com"),
                new Patient("Giuliano Sagese", "GiulianoSagese@dayrep.com"), //15
                new Patient("Cinzia Cattaneo", "CinziaCattaneo@jourrapide.com"),
                new Patient("Agenore Colombo", "AgenoreColombo@dayrep.com"),
                new Patient("Agata Sabbatini", "AgataSabbatini@armyspy.com"),
                new Patient("Luciana Manna", "LucianaManna@jourrapide.com"),
                new Patient("Luce Schiavone", "LuceSchiavone@dayrep.com") //20
            ).collect(Collectors.toList()));
    }

    // MESSAGE HANDLING
    @StreamListener(SagaStream.INPUT_SURGERY)
    public void handleSurgeryInboundEvent(@Payload NotifySurgeryEvent ev){

        System.out.println("Responding to event: " + ev.toString());

        Optional<Patient> op = patientRepository.findById(ev.getPatientId());
        if(op.isPresent()){
            Patient p = op.get();
            System.out.println(p); // ----------------------------
            if(ev.getAction()=="BOOK"){
                if(p.getStatus()=="FREE"){
                    System.out.println("Sending book to Booking"); // ----------------------------
                    this.handleBookingOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getTriage(), "BOOK");
                }else{
                    if(ev.getTriage()) this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_PATIENT_STATUS", false);
                    else this.handleSurgeryOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_PATIENT_STATUS", false);
                }
            }else if(ev.getAction()=="FREE"){
                if(p.getStatus()=="SURGERY" || p.getStatus()=="TRIAGE"){
                    System.out.println("Sending free to Booking"); // ----------------------------
                    this.handleBookingOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getTriage(), "FREE");
                }else{
                    if(ev.getTriage()) this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_PATIENT_STATUS", false);
                    else this.handleSurgeryOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_PATIENT_STATUS", false);
                }
            }
        }
        else{
            System.out.println("An unknown patient was requested: "+ ev.getPatientId());
            if(ev.getTriage()) this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_PATIENT", false);
            else this.handleSurgeryOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), "FAIL_PATIENT", false);
        }
    }

    @StreamListener(SagaStream.INPUT_BOOKING)
    public void handleBookingInboundEvent(@Payload FeedbackSurgeryEvent ev){

        System.out.println("Responding to event: " + ev.toString());

        Optional<Patient> op = patientRepository.findById(ev.getPatientId());
        if(op.isPresent()){
            Patient p = op.get();
            if(ev.getResult()=="SUCCESS"){
                if(ev.getTriage()) this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getAction(), true);
                else this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getAction(), true);
            
                if(ev.getAction()=="BOOK"){
                    p.setStatus(ev.getTriage() ? "TRIAGE" : "SURGERY");
                    p.setIdSurgery(ev.getSurgeryId());
                }else if(ev.getAction()=="FREE"){
                    p.setStatus("FREE");
                    p.setIdSurgery(-1);
                }
                p = patientRepository.save(p);
            }else if(ev.getResult()=="FAILURE"){
                if(ev.getTriage()) this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getAction(), false);
                else this.handleTriageOutboundEvent(ev.getSurgeryId(), ev.getPatientId(), ev.getBookingId(), ev.getAction(), false);
            }
            
        }else System.out.println("No patient found with id "+ ev.getPatientId().toString());

    }

    @SendTo(SagaStream.OUTPUT_TRIAGE)
    public void handleTriageOutboundEvent(Integer surgeryId, Integer patientId, Integer bookingId, String action, Boolean success){
        FeedbackSurgeryEvent ev = new FeedbackSurgeryEvent(surgeryId, patientId, bookingId, action, success, true);
        System.out.println("Sending out event (triage): " + ev.toString());
        this.stream.triageOutboundMessages().send(MessageBuilder.withPayload(ev).build());
    }

    @SendTo(SagaStream.OUTPUT_SURGERY)
    public void handleSurgeryOutboundEvent(Integer surgeryId, Integer patientId, Integer bookingId, String action, Boolean success){
        FeedbackSurgeryEvent ev = new FeedbackSurgeryEvent(surgeryId, patientId, bookingId, action, success, false);
        System.out.println("Sending out event (surgery): " + ev.toString());
        this.stream.surgeryOutboundMessages().send(MessageBuilder.withPayload(ev).build());
    }

    @SendTo(SagaStream.OUTPUT_BOOKING)
    public void handleBookingOutboundEvent(Integer surgeryId, Integer patientId, Integer bookingId, Boolean triage, String action){
        NotifySurgeryEvent ev = new NotifySurgeryEvent(surgeryId, patientId, bookingId, triage, action);
        System.out.println("Sending out event (booking): " + ev.toString());
        this.stream.bookingOutboundMessages().send(MessageBuilder.withPayload(ev).build());
    }
}
