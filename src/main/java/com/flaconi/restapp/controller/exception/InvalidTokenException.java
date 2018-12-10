package com.flaconi.restapp.controller.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Created by Liodegar.
 */
public class InvalidTokenException extends NestedRuntimeException {


    public InvalidTokenException() {
        super("The received token is invalid");
    }

    public InvalidTokenException(String message) {
        super(message);
    }


    public InvalidTokenException(String message, Throwable originalException) {
        super(message, originalException);
    }

}
