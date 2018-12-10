package com.flaconi.restapp.repository;

import com.flaconi.restapp.model.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = CategoryRepository.class)
@EntityScan(basePackageClasses = Category.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Sql(scripts = "classpath:script_test.sql")
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testGetCategoryByIdOrSlug() {
        //given conditions
        Category category = new Category(null, "col-000");
        category.setName("Cologne");

        //when
        Category retrievedCategory = categoryRepository.getCategoryByIdOrSlug(category);
        Assert.assertNull(retrievedCategory);

        Category savedCategory = categoryRepository.save(category);
        retrievedCategory = categoryRepository.getCategoryByIdOrSlug(category);

        //validate
        Assert.assertNotNull(retrievedCategory);
        assertEquals(savedCategory, retrievedCategory);

    }

    @Test
    public void testGetCategoryChildren() {
        //given conditions
        Category parentCategory = new Category(null, "col-000");
        parentCategory.setName("Cologne");

        Category category1 = new Category(null, "floral-col-001");
        category1.setName("Floral cologne");
        category1.setVisible(true);
        category1.setParentCategory(parentCategory);

        Category category2 = new Category(null, "spicy-col-001");
        category2.setName("Spicy cologne");
        category2.setVisible(true);
        category2.setParentCategory(parentCategory);

        Set<Category> categories = new HashSet<>();
        categories.add(category2);
        categories.add(category1);
        //when
        Set<Category> retrievedCategories = categoryRepository.getCategoryChildren(parentCategory);
        Assert.assertTrue(retrievedCategories.isEmpty());

        Category savedParentCategory = categoryRepository.save(parentCategory);
        category1.setParentCategory(savedParentCategory);
        category2.setParentCategory(savedParentCategory);

        category1 = categoryRepository.save(category1);
        category2 = categoryRepository.save(category2);
        retrievedCategories = categoryRepository.getCategoryChildren(savedParentCategory);

        //validate
        assertFalse(retrievedCategories.isEmpty());
        assertEquals(categories.stream().sorted(Comparator.comparing(Category::getSlug)).collect(Collectors.toList()),
                retrievedCategories.stream().sorted(Comparator.comparing(Category::getSlug)).collect(Collectors.toList()));

    }


    @Test
    public void testUpdateVisibility() {
        //given conditions
        Category category = new Category(null, "col-000");
        category.setName("Cologne");

        //when
        Category savedCategory = categoryRepository.save(category);
        Category retrievedCategory = categoryRepository.getCategoryByIdOrSlug(category);

        //validate
        assertFalse(savedCategory.isVisible());
        Assert.assertNotNull(retrievedCategory);

        savedCategory.setVisible(true);
        retrievedCategory = categoryRepository.getCategoryByIdOrSlug(category);
        assertTrue(retrievedCategory.isVisible());

        int result = categoryRepository.updateVisibility(savedCategory);
        retrievedCategory = categoryRepository.getCategoryByIdOrSlug(category);
        assertTrue(retrievedCategory.isVisible());

        assertEquals(1, result);

    }

}