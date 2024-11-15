package com.example.concertTicket_payment.payment.repository;

import com.example.concertTicket_payment.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByConcertIdAndConcertScheduleIdAndUuid(long concertId, long concertScheduleId, String uuid);
}
