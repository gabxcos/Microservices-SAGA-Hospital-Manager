package com.gcostanzoweb.Booking.config;

import com.gcostanzoweb.Booking.stream.SagaStream;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(SagaStream.class)
public class StreamConfig {
    
}
