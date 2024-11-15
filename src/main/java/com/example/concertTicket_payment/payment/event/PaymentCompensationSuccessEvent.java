package com.example.concertTicket_payment.payment.event;

import lombok.Getter;

@Getter
public class PaymentCompensationSuccessEvent {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private long seatNumber;
    private long price;
    private String message;

    public PaymentCompensationSuccessEvent(long concertId, long concertScheduleId, String uuid, long seatNumber, long price, String message){
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.seatNumber = seatNumber;
        this.price = price;
        this.message = message;
    }
}
