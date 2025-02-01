package com.example.concertTicket_payment.payment.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentOrderCompensationFailedEvent {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private long totalPrice;
    private String message;

    public PaymentOrderCompensationFailedEvent(long concertId, long concertScheduleId, String uuid, long totalPrice, String message){
        this.concertId = concertId;
        this.concertScheduleId = concertScheduleId;
        this.uuid = uuid;
        this.totalPrice = totalPrice;
        this.message = message;
    }
}
