package com.gcostanzoweb.Surgery.config;

import com.gcostanzoweb.Surgery.stream.SagaStream;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(SagaStream.class)
public class StreamConfig {
    
}
