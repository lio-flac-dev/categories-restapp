package com.flaconi.restapp.service;

import com.flaconi.restapp.model.Category;
import com.flaconi.restapp.repository.CategoryRepository;
import com.flaconi.restapp.service.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;


/**
 * Default impl of CategoryService
 * Created by Liodegar.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository categoryRepository;


    /**
     * Gets the category for the given ID or slug
     *
     * @param category The category instance containing either Id or slug
     * @return a Category instance
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    @Cacheable(value= "categoryCache", key="{#category.id, #category.slug}")
    public Category getCategoryByIdOrSlug(Category category) throws BusinessException {
        try {
            ensureKeyExclusivity(category);
            return categoryRepository.getCategoryByIdOrSlug(category);
        } catch (Exception e) {
            throw new BusinessException("Exception encountered invoking getCategoryById with param=" + category, e);
        }
    }


    /**
     * Creates a category
     *
     * @param category The category instance to be persisted
     * @return a Category instance
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    public Category createCategory(Category category) throws BusinessException {
        try {
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new BusinessException("Exception encountered creating the category=" + category, e);
        }
    }

    /**
     * Gets the children of the given category
     *
     * @param category the param Category instance to retrieve their children
     * @return a Category instance
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    @Cacheable(value= "categoryChildrenCache", key="{#category.id, #category.slug}",  unless= "#result.size() == 0")
    public Set<Category> getCategoryChildren(Category category) throws BusinessException {
        try {
            ensureKeyExclusivity(category);
            return categoryRepository.getCategoryChildren(category);
        } catch (Exception e) {
            throw new BusinessException("Exception encountered creating the category=" + category, e);
        }
    }

    /**
     * Updates the category visibility for the given category
     *
     * @param category The category instance to be updated
     * @return the number of modified records
     * @throws BusinessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Override
    public int updateVisibility(Category category) throws BusinessException {
        try {
            ensureKeyExclusivity(category);
            return categoryRepository.updateVisibility(category);
        } catch (Exception e) {
            throw new BusinessException("Exception encountered update the visibility category=" + category, e);
        }
    }

    private void ensureKeyExclusivity(Category category) {
        //This is to ensure the retrieving in an exclusive way
        if (category.getId() != null) {
            category.setSlug(null);
        }
    }


}
