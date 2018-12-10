package com.flaconi.restapp;

import com.flaconi.restapp.controller.CategoryController;
import com.flaconi.restapp.repository.CategoryRepository;
import com.flaconi.restapp.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryApplicationTests {

	@Autowired
	private CategoryController controller;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Test
	public void contexLoads() throws Exception {
		assertThat(controller).isNotNull();
		assertThat(categoryService).isNotNull();
		assertThat(categoryRepository).isNotNull();
	}

}
