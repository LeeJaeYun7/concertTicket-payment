package com.example.concertTicket_payment.payment.service;

import com.example.concertTicket_payment.payment.domain.Payment;
import com.example.concertTicket_payment.payment.event.PaymentConfirmedEvent;
import com.example.concertTicket_payment.payment.event.PaymentFailedEvent;
import com.example.concertTicket_payment.payment.event.PaymentRequestEvent;
import com.example.concertTicket_payment.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate kafkaTemplate;

    @Transactional
    @KafkaListener(topics = "payment-request-topic", groupId = "payment-service")
    public void createPayment(PaymentRequestEvent paymentRequestEvent){
        long concertId = paymentRequestEvent.getConcertId();
        long concertScheduleId = paymentRequestEvent.getConcertScheduleId();
        String uuid = paymentRequestEvent.getUuid();
        long seatNumber = paymentRequestEvent.getSeatNumber();
        long price = paymentRequestEvent.getPrice();

        try {
            boolean paymentSuccess = externalPaymentSystemCall(uuid, price);

            if (!paymentSuccess) {
                kafkaTemplate.send("payment-failed-topic", new PaymentFailedEvent(
                        concertId, concertScheduleId, uuid, seatNumber, price, "Payment system error"
                ));
                return;
            }

            Payment payment = Payment.of(concertId, concertScheduleId, uuid, price);
            paymentRepository.save(payment);

            kafkaTemplate.send("payment-confirmed-topic", new PaymentConfirmedEvent(
                    concertId, concertScheduleId, uuid, seatNumber, price));

        } catch (Exception e) {
            kafkaTemplate.send("payment-failed-topic", new PaymentFailedEvent(
                    concertId, concertScheduleId, uuid, seatNumber, price, "System error"
            ));
        }
    }
    private boolean externalPaymentSystemCall(String uuid, long price) {
        return false;
    }
}