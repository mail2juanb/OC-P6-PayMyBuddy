package com.ocP6.PayMyBuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnhandledException extends RuntimeException {
    public UnhandledException(String message) {
        super(message);
    }
}
