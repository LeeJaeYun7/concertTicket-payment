package com.example.concertTicket_payment.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentEntityDAO extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByConcertIdAndConcertScheduleIdAndUuid(long concertId, long concertScheduleId, String uuid);
}
