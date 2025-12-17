package com.grepp.datenow.infra.error.exception.course;

public class AlreadyDeactivatedException extends RuntimeException {

    public AlreadyDeactivatedException(String message) {
        super(message);
    }
}
