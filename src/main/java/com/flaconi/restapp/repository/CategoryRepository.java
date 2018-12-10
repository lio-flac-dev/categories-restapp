package com.flaconi.restapp.repository;

import com.flaconi.restapp.model.Category;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

/**
 * This repository exposes the category data access functionalities
 * Created by Liodegar.
 */
public interface CategoryRepository extends CrudRepository<Category, UUID> {

    /**
     * Retrieves the category for the given ID or slug from the underlying repository
     *
     * @param category The category instance with Id or slug properly set
     * @return a Category instance
     * @throws DataAccessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Query(value = "SELECT * FROM Category c WHERE (BIN_TO_UUID(c.id)=:#{#category.idAsString} " +
            " OR c.slug=:#{#category.slug}) ORDER BY c.slug",
            nativeQuery = true)
    Category getCategoryByIdOrSlug(@Param("category") Category category);


    /**
     * Gets the children of the given category
     *
     * @param category the Category param to retrieve their children
     * @return a Category instance
     * @throws DataAccessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Query(value = "SELECT * FROM Category c WHERE (BIN_TO_UUID(c.parent_category_id) IN (SELECT BIN_TO_UUID(innerCat.id) " +
            " FROM Category innerCat  WHERE (BIN_TO_UUID(innerCat.id)=:#{#category.idAsString} OR innerCat.slug=:#{#category.slug} ))) " +
            " ORDER BY c.slug",
            nativeQuery = true)
    Set<Category> getCategoryChildren(@Param("category") Category category);


    /**
     * c
     * Updates the category visibility for the given category
     *
     * @param category The category instance to be updated
     * @return the updated Category instance
     * @throws DataAccessException if any exception is found, with a message containing contextual information of the error and the root exception
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE Category c SET c.is_visible=:#{#category.visible} " +
            " WHERE (BIN_TO_UUID(c.id)=:#{#category.idAsString}  OR c.slug=:#{#category.slug} )", nativeQuery = true)
    int updateVisibility(@Param("category") Category category);

}
