package com.gcostanzoweb.Patient.controller;

import com.gcostanzoweb.Patient.entities.Patient;
import com.gcostanzoweb.Patient.entities.PatientRepository;
import com.gcostanzoweb.Patient.service.MainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping(path="/patient")
public class MainController {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MainService mainService;

    @PostMapping(path="/create")
    public @ResponseBody String createPatient(
        @RequestParam String name,
        @RequestParam String email) {

        Patient p = new Patient(name, email);
        patientRepository.save(p);
        
        return "Saved new patient: " + name + ", " + email + ".";
    }

    @GetMapping(path="/findAll")
    public @ResponseBody Iterable<Patient> getAllPatients() {

        return patientRepository.findAll();
    }
    
}
