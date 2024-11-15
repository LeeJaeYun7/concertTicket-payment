package com.example.concertTicket_payment.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST,"결제가 존재하지 않습니다."),

    PAYMENT_COMPENSATION_FAILED(HttpStatus.BAD_REQUEST,"보상 트랜잭션이 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}