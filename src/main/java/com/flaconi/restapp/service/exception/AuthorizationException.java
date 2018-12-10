package com.flaconi.restapp.service.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Unchecked exception to be thrown indicating an authorization issue.
 * Created by Liodegar on 10/13/2018.
 */
public class AuthorizationException extends NestedRuntimeException {


    /**
     * Constructs a AuthorizationException with the specified detail message.
     *
     * @param message Message intended for developers.
     */
    public AuthorizationException(String message) {
        super(message);
    }

    /**
     * Constructs a BusinessException with the specified detail message and the original exception.
     *
     * @param message           Message intended for developers.
     * @param originalException The nested exception.
     */
    public AuthorizationException(String message, Throwable originalException) {
        super(message, originalException);
    }

}
