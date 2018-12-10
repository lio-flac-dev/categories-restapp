package com.flaconi.restapp.controller.dto;

/**
 * Created by Liodegar.
 */
public final class ErrorCodeConstants {

    public static final String ILLEGAL_ARG_EXCEPTION = "illegalArgumentException";
    public static final String INVALID_TOKEN_EXCEPTION = "InvalidTokenException";
    public static final String BUSINESS_EXCEPTION = "businessException";
    public static final String AUTHORIZATION_EXCEPTION = "authorizationException";
    public static final String INTEGRATION_EXCEPTION = "integrationException";
    public static final String UNCATEGORIZED_EXCEPTION = "uncategorizedException";
    public static final String UNHANDLED_EXCEPTION = "unHandledException";


    /**
     * To avoid instantiation from outside
     */
    private ErrorCodeConstants() {
    }


}
