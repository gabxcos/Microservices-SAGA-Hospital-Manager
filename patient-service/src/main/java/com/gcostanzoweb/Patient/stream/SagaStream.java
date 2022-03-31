package com.gcostanzoweb.Patient.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface SagaStream {
    String INPUT_SURGERY = "input-surgery";
    String INPUT_BOOKING = "input-booking";
    String OUTPUT_TRIAGE = "output-triage";
    String OUTPUT_SURGERY = "output-surgery";
    String OUTPUT_BOOKING = "output-booking";

    @Input(INPUT_SURGERY)
    SubscribableChannel surgeryInboundMessages();

    @Input(INPUT_BOOKING)
    SubscribableChannel bookingInboundMessages();

    @Output(OUTPUT_TRIAGE)
    MessageChannel triageOutboundMessages();

    @Output(OUTPUT_SURGERY)
    MessageChannel surgeryOutboundMessages();

    @Output(OUTPUT_BOOKING)
    MessageChannel bookingOutboundMessages();
}
