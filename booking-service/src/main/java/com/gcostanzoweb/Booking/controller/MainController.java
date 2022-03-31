package com.gcostanzoweb.Booking.controller;

import com.gcostanzoweb.Booking.entities.Booking;
import com.gcostanzoweb.Booking.entities.BookingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping(path="/booking")
public class MainController {
    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping(path="/create")
    public @ResponseBody String createBooking(
        @RequestParam String name,
        @RequestParam Integer maxBusy) {

        Booking b = new Booking(name, maxBusy);
        b = bookingRepository.save(b);
        
        return "Saved new booking: " + b.toString();
    }

    @GetMapping(path="/findAll")
    public @ResponseBody Iterable<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }
    
}
