package com.flaconi.restapp.service;

import com.flaconi.restapp.model.Category;
import com.flaconi.restapp.service.exception.BusinessException;

import java.util.Set;
import java.util.UUID;

/**
 * This service exposes the core category business functionalities
 * Created by Liodegar.
 */
public interface CategoryService {

    /**
     * Gets the category for the given ID or slug
     *
     * @param category The category instance containing either Id or slug
     * @return a Category instance
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    Category getCategoryByIdOrSlug(Category category) throws BusinessException;


    /**
     * Creates a category
     *
     * @param category The category instance to be persisted
     * @return a Category instance
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    Category createCategory(Category category) throws BusinessException;

    /**
     * Gets the children of the given category
     *
     * @param category the param Category instance to retrieve their children
     * @return a Category instance
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */

    Set<Category> getCategoryChildren(Category category) throws BusinessException;

    /**
     * Updates the category visibility for the given category
     *
     * @param category The category instance to be updated
     * @return the number of modified records
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    int updateVisibility(Category category) throws BusinessException;

}
