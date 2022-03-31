package com.gcostanzoweb.Triage.controller;

import com.gcostanzoweb.Triage.entities.Surgery;
import com.gcostanzoweb.Triage.entities.SurgeryRepository;

import com.gcostanzoweb.Triage.service.MainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;


@Controller
@RequestMapping(path="/triage")
public class MainController {
    @Autowired
    private SurgeryRepository surgeryRepository;

    @Autowired
    private MainService mainService;

    @PostMapping(path="/create")
    public @ResponseBody String createSurgery(
        @RequestParam Integer idPatient,
        @RequestParam Integer idBooking,
        @RequestParam Integer priority) {

        Surgery s = new Surgery(idPatient, idBooking, priority);
        s = surgeryRepository.save(s);

        return "Saved new surgery: " + s.toString();
    }

    @GetMapping(path="/findAll")
    public @ResponseBody Iterable<Surgery> getAllSurgeries() {

        return surgeryRepository.findAll();
    }

    @DeleteMapping(path="/free")
    public @ResponseBody String freeSurgery(@RequestParam Integer id) {
        Optional<Surgery> os = surgeryRepository.findById(id);
        if(os.isPresent()){
            Surgery s = os.get();

            mainService.handleOutboundEvent(s.getId(), s.getIdPatient(), s.getIdBooking(), "FREE");

            return "Requested freeing surgery: " + s.toString();
        }
        else{
            return "No surgery with this id";
        }
    }
    
}
