package com.flaconi.restapp;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableSwagger2
@Profile("!test")
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.basePackage("com.flaconi.restapp.controller")).paths(PathSelectors.ant("/categoryApi/*"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.POST,
                        Arrays.asList(
                                new ResponseMessageBuilder()
                                        .code(500)
                                        .message("Internal Server Error into Categories Rest API")
                                        .responseModel(new ModelRef("Error"))
                                        .build(),

                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("API Request Forbidden!")
                                        .build(),

                                new ResponseMessageBuilder()
                                        .code(404)
                                        .message("Request API Not Found!")
                                        .build()

                        ));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Categories Microservice REST API",
                "These are categories service APIs.",
                "API 1.0",
                "https://help.github.com/articles/github-terms-of-service",
                new Contact("Liodegar Bracamonte", "http://www.flaconi.de", "liodegar@gmail.com"),
                "License of API", "License URL", Collections.emptyList());
    }

}
