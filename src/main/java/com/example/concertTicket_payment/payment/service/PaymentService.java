package com.example.concertTicket_payment.payment.service;

import com.example.concertTicket_payment.common.CustomException;
import com.example.concertTicket_payment.common.ErrorCode;
import com.example.concertTicket_payment.common.Loggable;
import com.example.concertTicket_payment.payment.domain.PaymentEntity;
import com.example.concertTicket_payment.payment.event.*;
import com.example.concertTicket_payment.payment.infrastructure.kafka.producer.PaymentOrderEventProducer;
import com.example.concertTicket_payment.payment.domain.PaymentEntityDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentEntityDAO paymentEntityDAO;
    private final PaymentOrderEventProducer paymentOrderEventProducer;

    @Transactional
    public void createPayment(OrderPaymentRequestEvent orderPaymentRequestEvent){

        log.info("createPayment 시작!");

        long concertId = orderPaymentRequestEvent.getConcertId();
        long concertScheduleId = orderPaymentRequestEvent.getConcertScheduleId();
        String uuid = orderPaymentRequestEvent.getUuid();
        List<Long> concertScheduleSeatIds = orderPaymentRequestEvent.getConcertScheduleSeatIds();
        long totalPrice = orderPaymentRequestEvent.getTotalPrice();

        try {
            boolean paymentSuccess = externalPaymentSystemCall(uuid, totalPrice);
            log.info("paymentSuccess는?", paymentSuccess);

            if (!paymentSuccess) {
                paymentOrderEventProducer.sendPaymentOrderFailedEvent("payment-order-failed-topic", new PaymentOrderFailedEvent(
                        concertId, concertScheduleId, uuid, totalPrice, "Payment system error"
                ));
                return;
            }

            PaymentEntity payment = PaymentEntity.of(concertId, concertScheduleId, uuid, totalPrice);
            paymentEntityDAO.save(payment);

            paymentOrderEventProducer.sendPaymentOrderConfirmedEvent("payment-order-confirmed-topic", new PaymentOrderConfirmedEvent(
                    concertId, concertScheduleId, uuid, concertScheduleSeatIds, totalPrice));

        } catch (Exception e) {
            paymentOrderEventProducer.sendPaymentOrderFailedEvent("payment-order-failed-topic", new PaymentOrderFailedEvent(
                    concertId, concertScheduleId, uuid, totalPrice, "System error"
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
    public void handleCompensationEvent(OrderPaymentCompensationEvent event) {
        long concertId = event.getConcertId();
        long concertScheduleId = event.getConcertScheduleId();
        String uuid = event.getUuid();
        long totalPrice = event.getTotalPrice();

        try {
            PaymentEntity payment = paymentEntityDAO.findByConcertIdAndConcertScheduleIdAndUuid(concertId, concertScheduleId, uuid)
                                               .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND, Loggable.ALWAYS));

            paymentEntityDAO.delete(payment);

            paymentOrderEventProducer.sendPaymentCompensationSuccessEvent("payment-order-compensation-success-topic", new PaymentOrderCompensationSuccessEvent(
                    concertId, concertScheduleId, uuid, totalPrice, "Payment canceled successfully"
            ));
        } catch (Exception e) {
            paymentOrderEventProducer.sendPaymentCompensationFailedEvent("payment-order-compensation-failed-topic", new PaymentOrderCompensationFailedEvent(
                    concertId, concertScheduleId, uuid, totalPrice, "Compensation failed"
            ));
        }
    }
}