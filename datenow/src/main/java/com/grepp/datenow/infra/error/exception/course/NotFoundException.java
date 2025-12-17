package com.grepp.datenow.infra.error.exception.course;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
