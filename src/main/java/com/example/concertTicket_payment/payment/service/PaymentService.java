package com.example.concertTicket_payment.payment.service;

import com.example.concertTicket_payment.common.CustomException;
import com.example.concertTicket_payment.common.ErrorCode;
import com.example.concertTicket_payment.common.Loggable;
import com.example.concertTicket_payment.payment.domain.Payment;
import com.example.concertTicket_payment.payment.event.*;
import com.example.concertTicket_payment.payment.infrastructure.kafka.producer.KafkaMessageProducer;
import com.example.concertTicket_payment.payment.infrastructure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Transactional
    public void createPayment(PaymentRequestEvent paymentRequestEvent){

        long concertId = paymentRequestEvent.getConcertId();
        long concertScheduleId = paymentRequestEvent.getConcertScheduleId();
        String uuid = paymentRequestEvent.getUuid();
        long seatNumber = paymentRequestEvent.getSeatNumber();
        long price = paymentRequestEvent.getPrice();

        try {
            boolean paymentSuccess = externalPaymentSystemCall(uuid, price);

            if (!paymentSuccess) {
                kafkaMessageProducer.sendPaymentFailedEvent("payment-failed-topic", new PaymentFailedEvent(
                        concertId, concertScheduleId, uuid, seatNumber, price, "Payment system error"
                ));
                return;
            }

            Payment payment = Payment.of(concertId, concertScheduleId, uuid, price);
            paymentRepository.save(payment);

            kafkaMessageProducer.sendPaymentConfirmedEvent("payment-confirmed-topic", new PaymentConfirmedEvent(
                    concertId, concertScheduleId, uuid, seatNumber, price));

        } catch (Exception e) {
            kafkaMessageProducer.sendPaymentFailedEvent("payment-failed-topic", new PaymentFailedEvent(
                    concertId, concertScheduleId, uuid, seatNumber, price, "System error"
            ));
        }
    }

    @Retryable(
            value = {CustomException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 3)
    )
    private boolean externalPaymentSystemCall(String uuid, long price) {
        return true;
    }

    @Transactional
    public void handleCompensationEvent(PaymentRequestEvent paymentRequestEvent) {
        long concertId = paymentRequestEvent.getConcertId();
        long concertScheduleId = paymentRequestEvent.getConcertScheduleId();
        String uuid = paymentRequestEvent.getUuid();
        long seatNumber = paymentRequestEvent.getSeatNumber();
        long price = paymentRequestEvent.getPrice();

        try {
            Payment payment = paymentRepository.findByConcertIdAndConcertScheduleIdAndUuid(concertId, concertScheduleId, uuid)
                    .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, Loggable.ALWAYS));

            paymentRepository.delete(payment);

            kafkaMessageProducer.sendPaymentCompensationSuccessEvent("payment-compensation-success-topic", new PaymentCompensationSuccessEvent(
                    concertId, concertScheduleId, uuid, seatNumber, price, "Payment canceled successfully"
            ));
        } catch (Exception e) {
            kafkaMessageProducer.sendPaymentCompensationFailedEvent("payment-compensation-failed-topic", new PaymentCompensationFailedEvent(
                    concertId, concertScheduleId, uuid, seatNumber, price, "Compensation failed"
            ));
        }
    }
}