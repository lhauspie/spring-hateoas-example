package com.lhauspie.example.hateoas;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.io.IOException;

@Configuration
// FIXME : Is it really useful ?
// FIXME : What's the purpose of this class ?
public class OpenApiValidationConfig implements WebMvcConfigurer {
    private final OpenApiValidationInterceptor validationInterceptor;

    @Autowired
    public OpenApiValidationConfig() throws IOException {
        this.validationInterceptor = new OpenApiValidationInterceptor(
                OpenApiInteractionValidator
                        .createFor(OpenApiValidationConfig.class.getResource("/swagger.yml").getPath())
                        .build()
        );
    }

    @Bean
    public Filter validationFilter() {
        return new OpenApiValidationFilter(
                true, // enable request validation
                true  // enable response validation
        );
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(validationInterceptor);
    }
}