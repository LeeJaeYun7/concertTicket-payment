package com.example.concertTicket_payment.payment.infrastructure.kafka.consumer;

import com.example.concertTicket_payment.payment.event.OrderPaymentCompensationEvent;
import com.example.concertTicket_payment.payment.event.OrderPaymentRequestEvent;
import com.example.concertTicket_payment.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPaymentEventConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-payment-request-topic")
    public void receiveOrderPaymentRequestEvent(String message) throws JsonProcessingException {
        OrderPaymentRequestEvent event = objectMapper.readValue(message, OrderPaymentRequestEvent.class);
        paymentService.createPayment(event);
    }

    @KafkaListener(topics = "order-payment-compensation-topic")
    public void receiveOrderPaymentCompensationEvent(String message) throws JsonProcessingException {
        OrderPaymentCompensationEvent event = objectMapper.readValue(message, OrderPaymentCompensationEvent.class);
        paymentService.handleCompensationEvent(event);
    }
}
