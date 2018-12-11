package com.flaconi.restapp.controller;

import com.flaconi.restapp.aop.TokenRequired;
import com.flaconi.restapp.controller.dto.CategoryDto;
import com.flaconi.restapp.controller.dto.ServiceRequest;
import com.flaconi.restapp.controller.dto.ServiceResponse;
import com.flaconi.restapp.controller.exception.ExceptionHandler;
import com.flaconi.restapp.model.Category;
import com.flaconi.restapp.service.CategoryService;
import com.flaconi.restapp.service.SecurityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This controller represents the entry point to the Category API
 * Created by Liodegar.
 */
@RestController
@RequestMapping("/categoryApi")
@Api(value = "/categoryApi", description = "Category Rest API Controller")
public class CategoryController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ModelMapper modelMapper;


    /**
     * Generates the token for the given user
     *
     * @param serviceRequest An instance of ServiceRequest<String> containing the subject
     * @return a ServiceResponse<String>> containing the generated token
     */
    @RequestMapping("/generateToken")
    @ApiOperation(value = "Generates the JWT", httpMethod = "POST")
    public ServiceResponse<String> generateToken(@RequestBody ServiceRequest<String> serviceRequest,
                                                 HttpServletResponse response) {
        ServiceResponse<String> serviceResponse = new ServiceResponse<>();
        try {
            checkParameter(serviceRequest);
            long sixMinutes = 6 * 1000 * 60;
            String token = securityService.generateToken(serviceRequest.getParameter(), sixMinutes);
            handleResponse(serviceResponse, token, response);
        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName(), response);
        }
        return serviceResponse;
    }

    /**
     * Creates a category
     *
     * @param serviceRequest An instance of ServiceRequest<CategoryDto> containing a CategoryDto instance
     * @return a ServiceResponse<CategoryDto> containing the created instance
     */
    @RequestMapping("/create")
    @TokenRequired
    @ApiOperation(value = "Creates a Category", httpMethod = "POST")
    public ServiceResponse<CategoryDto> createCategory(@RequestBody ServiceRequest<CategoryDto> serviceRequest,
                                                       HttpServletResponse response) {
        ServiceResponse<CategoryDto> serviceResponse = new ServiceResponse<>();
        try {
            checkCategoryDtoParameter(serviceRequest);
            Category categoryParam = convertToEntity(serviceRequest.getParameter());
            Category categoryResult = categoryService.createCategory(categoryParam);
            handleResponse(serviceResponse, convertToDto(categoryResult), response);

        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName(), response);
        }
        return serviceResponse;
    }


    /**
     * Gets a category, either by Id or slug. If both keys are present, the search is done by using the Id.
     *
     * @param categoryId The category Id
     * @param slug       The category slug
     * @return a ServiceResponse<CategoryDto> containing the created instance
     */
    @GetMapping("/category")
    @TokenRequired
    @ApiOperation(value = "Gets a Category", httpMethod = "GET")
    public ServiceResponse<CategoryDto> getCategory(@RequestParam(value = "categoryId", required = false) String categoryId,
                                                    @RequestParam(value = "slug", required = false) String slug,
                                                    HttpServletResponse response) {
        ServiceResponse<CategoryDto> serviceResponse = new ServiceResponse<>();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>();
        try {
            checkRequiredParameters(categoryId, slug);
            serviceRequest.setParameter(createDto(categoryId, slug));
            Category categoryResult = categoryService.getCategoryByIdOrSlug(convertToEntity(serviceRequest.getParameter()));
            handleResponse(serviceResponse, convertToDto(categoryResult), response, true);
        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName(), response);
        }
        return serviceResponse;
    }

    /**
     * Gets a category children belonging to the given Category.
     * The category should contain either Id or slug. If both keys are present, the search is done by using the Id.
     *
     * @param categoryId The category Id
     * @param slug       The category slug
     * @return a ServiceResponse<CategoryDto> containing the created instance
     */
    @GetMapping("/getCategoryChildren")
    @TokenRequired
    @ApiOperation(value = "Gets the children of a given category", httpMethod = "GET")
    public ServiceResponse<Set<CategoryDto>> getCategoryChildren(@RequestParam(value = "categoryId", required = false) String categoryId,
                                                                 @RequestParam(value = "slug", required = false) String slug,
                                                                 HttpServletResponse response) {
        ServiceResponse<Set<CategoryDto>> serviceResponse = new ServiceResponse<>();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>();
        try {
            checkRequiredParameters(categoryId, slug);
            serviceRequest.setParameter(createDto(categoryId, slug));
            Set<Category> children = categoryService.getCategoryChildren(convertToEntity(serviceRequest.getParameter()));

            Set<CategoryDto> childrenDto = children.stream()
                    .map(category -> convertToDto(category))
                    .collect(Collectors.toSet());
            handleResponse(serviceResponse, childrenDto, response, true);

        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName(), response);
        }
        return serviceResponse;
    }

    /**
     * Updates the  category visibility.
     * The category should contain either Id or slug. If both keys are present, the search is done by using the Id.
     *
     * @param serviceRequest An instance of ServiceRequest<CategoryDto> containing a CategoryDto instance
     * @return a ServiceResponse<CategoryDto> containing the created instance
     */
    @PatchMapping("/updateVisibility")
    @TokenRequired
    @ApiOperation(value = "Updates then visibility of a given Category", httpMethod = "PATCH")
    public ServiceResponse<CategoryDto> updateVisibility(@RequestBody ServiceRequest<CategoryDto> serviceRequest,
                                                         HttpServletResponse response) {
        ServiceResponse<CategoryDto> serviceResponse = new ServiceResponse<>();
        try {
            checkCategoryDtoParameter(serviceRequest);
            Category categoryParam = convertToEntity(serviceRequest.getParameter());
            int updatedRecords = categoryService.updateVisibility(categoryParam);
            handleResponse(serviceResponse, updatedRecords == 0 ? null : serviceRequest.getParameter(), response);

        } catch (Exception e) {
            exceptionHandler.handleControllerException(serviceResponse, serviceRequest, e, getMethodName(), response);
        }
        return serviceResponse;
    }


    /**
     * Converts an entity object to its DTO representation
     *
     * @param category The Category entity
     * @return a CategoryDto instance
     */
    protected CategoryDto convertToDto(Category category) {
        if (category == null) {
            return null;
        }
        return modelMapper.map(category, CategoryDto.class);
    }

    /**
     * Converts an DTO object to its Entity representation
     *
     * @param categoryDto the CategoryDto instance
     * @return a Category entity
     */
    protected Category convertToEntity(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return modelMapper.map(categoryDto, Category.class);
    }

    /**
     * Create an DTO object by using its basic parameters
     *
     * @param categoryId the category Id
     * @param slug       the category slug
     * @return a Category entity
     */
    private CategoryDto createDto(String categoryId, String slug) {
        return new CategoryDto(categoryId, slug);
    }
}