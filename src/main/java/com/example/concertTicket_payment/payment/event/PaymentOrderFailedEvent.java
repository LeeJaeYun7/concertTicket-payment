package com.example.concertTicket_payment.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrderFailedEvent {

    private long concertId;
    private long concertScheduleId;
    private String uuid;
    private long totalPrice;
    private String errorMessage;
}
