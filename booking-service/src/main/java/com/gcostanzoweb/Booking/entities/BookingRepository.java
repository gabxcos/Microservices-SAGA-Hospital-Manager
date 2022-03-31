package com.gcostanzoweb.Booking.entities;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@Repository
public interface BookingRepository extends CrudRepository<Booking, Integer>{
    
}
