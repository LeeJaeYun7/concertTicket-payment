package com.example.concertTicket_payment.payment.domain;

import com.example.concertTicket_payment.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payment")
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "concert_id")
    private long concertId;

    @Column(name = "concert_schedule_id")
    private long concertScheduleId;

    private String uuid;

    private long amount;

    @Builder
    public Payment(long concertId, long concertScheduleId, String uuid, long amount){
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.amount = amount;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static Payment of(long concertId, long concertScheduleId, String uuid, long amount){
        return Payment.builder()
                .concertId(concertId)
                .concertScheduleId(concertScheduleId)
                .uuid(uuid)
                .amount(amount)
                .build();
    }
}
