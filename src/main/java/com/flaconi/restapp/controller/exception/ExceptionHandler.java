package com.flaconi.restapp.controller.exception;

import com.flaconi.restapp.controller.dto.*;
import com.flaconi.restapp.service.exception.AuthorizationException;
import com.flaconi.restapp.service.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Class for handling exception thrown by the controller
 * Created by Liodegar.
 */
@Component
public class ExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSource messageSource;

    /**
     * Translates the different errors and exceptions to a higher level to be consumed by the client.
     * It maps the given exception to a specific HTTP status code
     * @param serviceResponse Generic DTO instance to be used as response for the controller
     * @param serviceRequest An instance of ServiceRequest<T> containing the actual object as parameter
     * @param e The actual exception encountered
     * @param method The actual method where the exception was encountered
     * @param response
     */
    public <T, E> void handleControllerException(ServiceResponse<T> serviceResponse, ServiceRequest<E> serviceRequest, Exception e, String method, HttpServletResponse response) {
        ResponseCodeEnum code = ResponseCodeEnum.ERROR;
        String message = null;
        try {

            if (e instanceof IllegalArgumentException) {
                message = getLocaleMessage(ErrorCodeConstants.ILLEGAL_ARG_EXCEPTION, new Object[]{method});
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            } else if (e instanceof BusinessException) {
                message = getLocaleMessage(ErrorCodeConstants.BUSINESS_EXCEPTION, new Object[]{serviceRequest, method});
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            } else if (e instanceof AuthorizationException) {
                message = getLocaleMessage(ErrorCodeConstants.AUTHORIZATION_EXCEPTION, new Object[]{serviceRequest, method});
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            } else if (e instanceof DataAccessException) {
                message = getLocaleMessage(ErrorCodeConstants.INTEGRATION_EXCEPTION, new Object[]{serviceRequest, method});
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

            } else if (e instanceof InvalidTokenException) {
                message = getLocaleMessage(ErrorCodeConstants.INVALID_TOKEN_EXCEPTION, new Object[]{serviceRequest, method});
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                message = getLocaleMessage(ErrorCodeConstants.UNCATEGORIZED_EXCEPTION, new Object[]{serviceRequest, method});
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            logAndHandleError(serviceResponse, e, code, message);
        } catch (Exception unknownException) {
            message = getLocaleMessage(ErrorCodeConstants.UNHANDLED_EXCEPTION, new Object[]{unknownException.getClass().getName(), serviceRequest, method});
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logAndHandleError(serviceResponse, e, code, message);
        }
    }

    private String getLocaleMessage(String key, Object[] args) {
        return messageSource.getMessage(key, args, Locale.US);
    }

    private void logAndHandleError(ServiceResponse serviceResponse, Exception e, ResponseCodeEnum code, String message) {
        ServiceError serviceError = new ServiceError(message, e);
        serviceResponse.setCode(code);
        serviceResponse.setError(serviceError);
        logger.error(String.valueOf(serviceError), e);
    }


}
