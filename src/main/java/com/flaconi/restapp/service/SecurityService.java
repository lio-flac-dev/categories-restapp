package com.flaconi.restapp.service;

import com.flaconi.restapp.service.exception.AuthorizationException;

/**
 * Defines the contract of the security functionalities
 * Created by Liodegar.
 */
public interface SecurityService {

    /**
     * Generates a token if the user is valid
     *
     * @param subject The subject requires
     * @param ttlMillis expiration time in millis
     * @return a token if the user is valid, throws an exception otherwise
     * @throws AuthorizationException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    String generateToken(String subject, long ttlMillis) throws AuthorizationException;
}
