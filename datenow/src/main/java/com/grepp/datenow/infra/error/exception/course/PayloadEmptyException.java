package com.grepp.datenow.infra.error.exception.course;

public class PayloadEmptyException extends RuntimeException {

    public PayloadEmptyException(String message) {
        super(message);
    }
}
