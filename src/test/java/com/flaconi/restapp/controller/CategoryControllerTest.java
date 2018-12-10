package com.flaconi.restapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flaconi.restapp.CategoryApplication;
import com.flaconi.restapp.controller.dto.CategoryDto;
import com.flaconi.restapp.controller.dto.ResponseCodeEnum;
import com.flaconi.restapp.controller.dto.ServiceRequest;
import com.flaconi.restapp.controller.dto.ServiceResponse;
import com.flaconi.restapp.controller.exception.ExceptionHandler;
import com.flaconi.restapp.model.Category;
import com.flaconi.restapp.service.CategoryService;
import com.flaconi.restapp.service.SecurityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


/**
 * Test class to validate the proper functionality of CategoryController
 * Created by Liodegar
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CategoryController.class)
@ContextConfiguration(classes= CategoryApplication.class)
public class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private ExceptionHandler exceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    //generateToken
    private JacksonTester<ServiceResponse<String>> jsonResponseToken;

    private JacksonTester<ServiceRequest<String>> jsonRequestToken;

    //createCategory, getCategoryChildren
    private JacksonTester<ServiceRequest<CategoryDto>> jsonRequestCategoryDto;

    private JacksonTester<ServiceResponse<CategoryDto>> jsonResponseCategoryDto;

    private JacksonTester<ServiceResponse<Set<CategoryDto>>> jsonResponseSetCategoryDto;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void testGenerateTokenWithoutCredentials() throws Exception {
        // given
        String token = "eyJ0eJ9.eyJ1c2VyifQ.7xH-x5mb11yt3rGzcM";
        // when
        when(securityService.generateToken(anyString(), anyLong())).thenReturn(token);
        MockHttpServletResponse response = mockMvc.perform(post("/categoryApi/generateToken")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void testGenerateTokenWithoutCredentialsAndWithoutCsrf() throws Exception {
        // given
        String token = "eyJ0eJ9.eyJ1c2VyifQ.7xH-x5mb11yt3rGzcM";
        // when
        when(securityService.generateToken(anyString(), anyLong())).thenReturn(token);
        MockHttpServletResponse response = mockMvc.perform(post("/categoryApi/generateToken")
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }


    @Test
    @WithMockUser
    public void testGenerateTokenWithValidCredentials() throws Exception {
        // given
        String token = "eyJ0eJ9.eyJ1c2VyifQ.7xH-x5mb11yt3rGzcM";
        String subject = "categorySubject";
        ServiceRequest<String> serviceRequest = new ServiceRequest<>(subject);

        // when
        when(securityService.generateToken(anyString(), anyLong())).thenReturn(token);
        MockHttpServletResponse response = mockMvc.perform(post("/categoryApi/generateToken")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestToken.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseToken.write(new ServiceResponse(ResponseCodeEnum.SUCCESS, token)).getJson(), response.getContentAsString());
    }


    @Test
    public void testGetCategoryWithoutCredentials() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.getCategoryByIdOrSlug(any(Category.class))).thenReturn(new Category());

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/category")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void testGetCategoryWithoutCredentialsAndWithoutCsrf() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.getCategoryByIdOrSlug(any(Category.class))).thenReturn(new Category());

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }


    @Test
    @WithMockUser
    public void testGetCategoryWithInvalidParameters() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.getCategoryByIdOrSlug(any(Category.class))).thenReturn(new Category());

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/category")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.UNSUCCESS)).getJson(),
                response.getContentAsString());
    }

    @Test
    @WithMockUser
    public void testGetCategoryWithValidParameters() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);
        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "slug-1");

        //when
        when(categoryService.getCategoryByIdOrSlug(any(Category.class))).thenReturn(category);

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/category")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .param("categoryId", category.getIdAsString())
                .param("slug", category.getSlug()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.SUCCESS, category)).getJson(),
                response.getContentAsString());
    }


    @Test
    public void testGetCategoryChildrenWithoutCredentials() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);
        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "slug-1");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        //when
        when(categoryService.getCategoryChildren(any(Category.class))).thenReturn(categories);

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/getCategoryChildren")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }


    @Test
    @WithMockUser
    public void testGetCategoryChildrenWithInvalidParameters() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);
        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "slug-1");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        //when
        when(categoryService.getCategoryChildren(any(Category.class))).thenReturn(categories);

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/getCategoryChildren")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.UNSUCCESS)).getJson(),
                response.getContentAsString());
    }

    @Test
    @WithMockUser
    public void testGetCategoryChildrenWithValidParameters() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);
        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "slug-1");
        Set<Category> categories = new HashSet<>();
        categories.add(category);

        //when
        when(categoryService.getCategoryChildren(any(Category.class))).thenReturn(categories);

        MockHttpServletResponse response = mockMvc.perform(get("/categoryApi/getCategoryChildren")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .param("categoryId", category.getIdAsString())
                .param("slug", category.getSlug()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.SUCCESS, categories)).getJson(),
                response.getContentAsString());
    }


    @Test
    public void testCreateWithoutCredentials() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.createCategory(any(Category.class))).thenReturn(new Category());

        MockHttpServletResponse response = mockMvc.perform(post("/categoryApi/create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @WithMockUser
    public void testCreateWithInvalidParameter() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.createCategory(any(Category.class))).thenReturn(new Category());

        MockHttpServletResponse response = mockMvc.perform(post("/categoryApi/create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.UNSUCCESS)).getJson(),
                response.getContentAsString());
    }

    @Test
    @WithMockUser
    public void testCreateWithValidParameter() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9", "slug-1");
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);
        Category category = new Category(UUID.fromString(categoryDto.getId()), categoryDto.getSlug());

        //when
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        MockHttpServletResponse response = mockMvc.perform(post("/categoryApi/create")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.SUCCESS, categoryDto)).getJson(),
                response.getContentAsString());
    }


    @Test
    public void testUpdateVisibilityWithoutCredentials() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.createCategory(any(Category.class))).thenReturn(new Category());

        MockHttpServletResponse response = mockMvc.perform(patch("/categoryApi/updateVisibility")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }


    @Test
    @WithMockUser
    public void testUpdateVisibilityWithInvalidParameter() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto();
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.updateVisibility(any(Category.class))).thenReturn(0);

        MockHttpServletResponse response = mockMvc.perform(patch("/categoryApi/updateVisibility")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.UNSUCCESS)).getJson(),
                response.getContentAsString());
    }


    @Test
    @WithMockUser
    public void testUpdateVisibilityWithValidParameter() throws Exception {
        //given
        CategoryDto categoryDto = new CategoryDto("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9", "slug-1");
        ServiceRequest<CategoryDto> serviceRequest = new ServiceRequest<>(categoryDto);

        //when
        when(categoryService.updateVisibility(any(Category.class))).thenReturn(1);

        MockHttpServletResponse response = mockMvc.perform(patch("/categoryApi/updateVisibility")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(jsonRequestCategoryDto.write(serviceRequest).getJson()))
                .andReturn().getResponse();

        // then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jsonResponseCategoryDto.write(new ServiceResponse(ResponseCodeEnum.SUCCESS, categoryDto)).getJson(),
                response.getContentAsString());
    }


    @Test
    public void testConvertToDto() {
        Category rootCategory = new Category(UUID.fromString("2b5aa221-5a19-461a-b83e-10e03b676939"), "root-000");
        rootCategory.setName("Root category");

        Category parentCategory = new Category(UUID.fromString("7892955c-8755-4661-bf6b-9acc6b8f2ff4"), "col-000");
        parentCategory.setName("Cologne");
        parentCategory.setParentCategory(rootCategory);

        Category category = new Category(UUID.fromString("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9"), "floral-col-001");
        category.setName("Floral cologne");
        category.setVisible(true);
        category.setParentCategory(parentCategory);

        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
        assertEquals(String.valueOf(category.getId()), categoryDto.getId());
        assertEquals(category.getName(), categoryDto.getName());
        assertEquals(category.isVisible(), categoryDto.isVisible());

        assertEquals(String.valueOf(category.getParentCategory().getId()), categoryDto.getParentCategory().getId());
        assertEquals(category.getParentCategory().getName(), categoryDto.getParentCategory().getName());

        CategoryDto rootCategoryDto = categoryDto.getParentCategory().getParentCategory();
        assertEquals(String.valueOf(rootCategoryDto.getId()), rootCategoryDto.getId());
        assertEquals(rootCategoryDto.getName(), rootCategoryDto.getName());
        assertEquals(rootCategoryDto.isVisible(), rootCategoryDto.isVisible());
        assertEquals(rootCategoryDto.getParentCategory(), rootCategoryDto.getParentCategory());

    }

    @Test
    public void testConvertToEntity() {
        CategoryDto rootCategoryDto = new CategoryDto("2b5aa221-5a19-461a-b83e-10e03b676939", "root-000");
        rootCategoryDto.setName("Root category");

        CategoryDto parentCategoryDto = new CategoryDto("7892955c-8755-4661-bf6b-9acc6b8f2ff4", "col-000");
        parentCategoryDto.setName("Cologne");
        parentCategoryDto.setParentCategory(rootCategoryDto);

        CategoryDto categoryDto = new CategoryDto("0d3114cf-f382-4298-8dd1-b4b4e6e1a3e9", "floral-col-001");
        categoryDto.setName("Floral cologne");
        categoryDto.setVisible(true);
        categoryDto.setParentCategory(parentCategoryDto);


        Category category = modelMapper.map(categoryDto, Category.class);
        assertEquals(String.valueOf(category.getId()), categoryDto.getId());
        assertEquals(category.getName(), categoryDto.getName());
        assertEquals(category.isVisible(), categoryDto.isVisible());

        assertEquals(String.valueOf(category.getParentCategory().getId()), categoryDto.getParentCategory().getId());
        assertEquals(category.getParentCategory().getName(), categoryDto.getParentCategory().getName());

        Category rootCategory = category.getParentCategory().getParentCategory();
        assertEquals(String.valueOf(rootCategory.getId()), rootCategoryDto.getId());
        assertEquals(rootCategory.getName(), rootCategoryDto.getName());
        assertEquals(rootCategory.isVisible(), rootCategoryDto.isVisible());
        assertEquals(rootCategory.getParentCategory(), rootCategoryDto.getParentCategory());

    }

}