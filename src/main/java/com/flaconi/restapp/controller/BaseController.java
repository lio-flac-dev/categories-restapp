package com.flaconi.restapp.controller;


import com.flaconi.restapp.controller.dto.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Common controller class to extend any spring controller in the application
 */
public abstract class BaseController {

    public static final String CACHE_CONTROL = "Cache-Control";

    protected <T> void checkParameter(ServiceRequest<T> serviceRequest) {
        if (serviceRequest == null || serviceRequest.getParameter() == null) {
            throw new IllegalArgumentException("The required parameter is null");
        }
    }

    protected void checkCategoryDtoParameter(ServiceRequest<CategoryDto> serviceRequest) {
        checkParameter(serviceRequest);
        CategoryDto categoryDto = serviceRequest.getParameter();
        checkRequiredParameters(categoryDto.getId(), categoryDto.getSlug());
    }

    protected void checkRequiredParameters(String categoryId, String slug) {
        if (categoryId == null && slug == null) {
            throw new IllegalArgumentException(String.format("The received required keys are invalid.[id= %s, slug=%s]", categoryId, slug));
        }
    }

    protected <T> void handleResponse(ServiceResponse<T> serviceResponse, T result, HttpServletResponse response) {
        handleResponse(serviceResponse, result, response, false);
    }

    protected <T> void handleResponse(ServiceResponse<T> serviceResponse, T result, HttpServletResponse response, boolean cacheable) {
        if (result == null || (result instanceof Collection && ((Collection) result).isEmpty())) {
            serviceResponse.setCode(ResponseCodeEnum.UNSUCCESS);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            serviceResponse.setCode(ResponseCodeEnum.SUCCESS);
            serviceResponse.setResult(result);
            response.setStatus(HttpServletResponse.SC_OK);
            if (cacheable) {
                response.setHeader(CACHE_CONTROL, "max-age=14400");
            }
        }
    }

    /**
     * Returns the name of the current method being executed
     *
     * @return a String object
     */
    protected String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
