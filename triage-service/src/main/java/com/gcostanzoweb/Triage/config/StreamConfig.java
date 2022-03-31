package com.gcostanzoweb.Triage.config;

import com.gcostanzoweb.Triage.stream.SagaStream;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(SagaStream.class)
public class StreamConfig {
    
}
