package com.flaconi.restapp.service;

import com.flaconi.restapp.model.Category;
import com.flaconi.restapp.repository.CategoryRepository;
import com.flaconi.restapp.service.exception.BusinessException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * Test case for validating the proper functionality of CategoryService
 * Created by Liodegar.
 */
@RunWith(SpringRunner.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    @InjectMocks
    private CategoryServiceImpl systemUnderTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    /**
     * Tests the getCategoryByIdOrSlug method when the repository finds a proper Category
     */
    @Test
    public void testGetCategoryByIdOrSlug() {
        //given conditions
        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");

        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category.setName("Floral cologne");
        category.setVisible(true);
        category.setParentCategory(parentCategory);

        //when
        doReturn(category).when(categoryRepository).getCategoryByIdOrSlug(any(Category.class));

        //validate
        Category result = systemUnderTest.getCategoryByIdOrSlug(category);
        assertNotNull(result);
        assertEquals(category, result);
        assertEquals(category.getParentCategory(), result.getParentCategory());

    }

    /**
     * Tests the getCategoryByIdOrSlug method when the repository doesn't find a proper Category
     */
    @Test
    public void testGetCategoryByIdOrSlugWhenThereIsNotCategories() {
        //given conditions
        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");

        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category.setName("Floral cologne");
        category.setVisible(true);
        category.setParentCategory(parentCategory);

        //when
        doReturn(null).when(categoryRepository).getCategoryByIdOrSlug(any(Category.class));

        //validate
        Category result = systemUnderTest.getCategoryByIdOrSlug(category);
        assertNull(result);

    }

    /**
     * Tests the createCategory method when the repository properly saves a Category
     */
    @Test
    public void testCreateCategory() {
        //given conditions
        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");

        Category param = new Category();
        param.setSlug("floral-col-001");
        param.setName("Floral cologne");
        param.setVisible(true);
        param.setParentCategory(parentCategory);

        Category savedCategory = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), param.getSlug());
        savedCategory.setName(param.getName());
        savedCategory.setVisible(param.isVisible());
        savedCategory.setParentCategory(parentCategory);

        //when
        doReturn(savedCategory).when(categoryRepository).save(any(Category.class));

        //validate
        Category result = systemUnderTest.createCategory(param);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(param.getSlug(), result.getSlug());
        assertEquals(param.isVisible(), result.isVisible());
        assertEquals(param.getName(), result.getName());
        assertEquals(param.getParentCategory(), result.getParentCategory());

    }

    /**
     * Tests the createCategory method when the repository throws an exception while saving the category
     */
    @Test(expected = BusinessException.class)
    public void testCreateCategoryWithDataAccessException() {
        //given conditions
        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");

        Category param = new Category();
        param.setSlug("floral-col-001");
        param.setName("Floral cologne");
        param.setVisible(true);
        param.setParentCategory(parentCategory);

        //when
        doThrow(new CannotGetJdbcConnectionException("DB connection refused")).when(categoryRepository).save(any(Category.class));

        //validate
        Category result = systemUnderTest.createCategory(param);

    }

    /**
     * Tests the getCategoryChildren method when the repository finds the children
     */
    @Test
    public void testGetCategoryChildren() {
        //given conditions
        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");

        Category category1 = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category1.setName("Floral cologne");
        category1.setVisible(true);
        category1.setParentCategory(parentCategory);

        Category category2 = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category2.setName("Floral cologne");
        category2.setVisible(true);
        category2.setParentCategory(parentCategory);

        Set<Category> categories = new HashSet<>();
        categories.add(category1);
        categories.add(category2);

        //when
        doReturn(categories).when(categoryRepository).getCategoryChildren(any(Category.class));

        //validate
        Set<Category> result = systemUnderTest.getCategoryChildren(parentCategory);
        assertNotNull(result);
        assertEquals(categories, result);

    }

    /**
     * Tests the getCategoryChildren method when the repository throws an exception while saving the category
     */
    @Test(expected = BusinessException.class)
    public void testGetCategoryChildrenWithDataAccessException() {
        //given conditions
        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");
        //when
        doThrow(new CannotGetJdbcConnectionException("DB connection refused")).when(categoryRepository).getCategoryChildren(any(Category.class));

        //validate
        Set<Category> result = systemUnderTest.getCategoryChildren(parentCategory);
    }

    /**
     * Tests the updateVisibility method when the repository properly updates the visibility
     */
    @Test
    public void testUpdateVisibility() {
        //given conditions
        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category.setVisible(true);

        //when
        doReturn(1).when(categoryRepository).updateVisibility(any(Category.class));

        //validate
        int result = systemUnderTest.updateVisibility(category);
        assertEquals(1, result);

    }

    /**
     * Tests the updateVisibility method when the repository properly updates the visibility
     */
    @Test(expected = BusinessException.class)
    public void testUpdateVisibilityWithDataAccessException() {
        //given conditions
        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category.setVisible(true);

        //when
        doThrow(new CannotGetJdbcConnectionException("DB connection refused")).when(categoryRepository).updateVisibility(any(Category.class));

        //validate
        int result = systemUnderTest.updateVisibility(category);

    }

}