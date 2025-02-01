package com.example.concertTicket_payment.payment.infrastructure.kafka.producer;

import com.example.concertTicket_payment.payment.event.PaymentOrderCompensationFailedEvent;
import com.example.concertTicket_payment.payment.event.PaymentOrderCompensationSuccessEvent;
import com.example.concertTicket_payment.payment.event.PaymentOrderConfirmedEvent;
import com.example.concertTicket_payment.payment.event.PaymentOrderFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

@Component
@RequiredArgsConstructor
public class PaymentOrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendPaymentOrderConfirmedEvent(String topic, PaymentOrderConfirmedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }

    public void sendPaymentOrderFailedEvent(String topic, PaymentOrderFailedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }

    public void sendPaymentCompensationSuccessEvent(String topic, PaymentOrderCompensationSuccessEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }

    public void sendPaymentCompensationFailedEvent(String topic, PaymentOrderCompensationFailedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }
}
