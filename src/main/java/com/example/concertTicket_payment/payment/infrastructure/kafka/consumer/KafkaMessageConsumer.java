package com.example.concertTicket_payment.payment.infrastructure.kafka.consumer;

import com.example.concertTicket_payment.payment.event.PaymentRequestEvent;
import com.example.concertTicket_payment.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-request-topic")
    public void receivePaymentRequestEvent(String message) throws JsonProcessingException {
        PaymentRequestEvent event = objectMapper.readValue(message, PaymentRequestEvent.class);
        paymentService.createPayment(event);
    }

    @KafkaListener(topics = "payment-compensation-topic")
    public void receivePaymentCompensationEvent(String message) throws JsonProcessingException {
        PaymentRequestEvent event = objectMapper.readValue(message, PaymentRequestEvent.class);
        paymentService.handleCompensationEvent(event);
        System.out.println(message);
    }
}
