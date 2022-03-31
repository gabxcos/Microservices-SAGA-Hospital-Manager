package com.gcostanzoweb.Patient.config;

import com.gcostanzoweb.Patient.stream.SagaStream;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(SagaStream.class)
public class StreamConfig {
    
}
