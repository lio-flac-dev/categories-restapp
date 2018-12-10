package com.flaconi.restapp;


import io.swagger.annotations.Api;
import org.modelmapper.*;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * Spring Boot Application class with the required configuration and beans
 */
@SpringBootApplication
@EnableCaching
public class CategoryApplication {

    /**
     * The default charset to use for parsing properties files
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * The single basename, following the basic ResourceBundle convention of not specifying file extension or language codes
     */
    private static final String MESSAGE_BASE_NAME = "classpath:static/messages";

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(MESSAGE_BASE_NAME);
        messageSource.setDefaultEncoding(DEFAULT_ENCODING);
        return messageSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(CategoryApplication.class, args);
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public FilterRegistrationBean shallowEtagHeaderFilterRegistration() {
        FilterRegistrationBean result = new FilterRegistrationBean();
        result.setFilter(this.shallowEtagHeaderFilter());
        result.addUrlPatterns("/categoryApi/*");
        result.setName("shallowEtagHeaderFilter");
        result.setOrder(1);
        return result;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        Converter<String, UUID> stringToUuid = new AbstractConverter<String, UUID>() {
            @Override
            protected UUID convert(String source) {
                if (source == null) {
                    return null;
                }
                return UUID.fromString(source);
            }
        };
        modelMapper.createTypeMap(String.class, UUID.class);
        modelMapper.addConverter(stringToUuid);
        return modelMapper;
    }
}
