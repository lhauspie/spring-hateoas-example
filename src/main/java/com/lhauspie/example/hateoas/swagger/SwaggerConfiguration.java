package com.lhauspie.example.hateoas.swagger;

import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    @ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "false")
    public SpringDocConfiguration springDocConfiguration() {
        return new SpringDocConfiguration();
    }

    @Bean
    @ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "false")
    public SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }


    @Bean // Needed by org.springdoc:springdoc-openapi-ui:1.6.11
    @ConditionalOnProperty(value = "springdoc.api-docs.enabled", havingValue = "false")
    public ObjectMapperProvider objectMapperProvider() {
        return new ObjectMapperProvider(springDocConfigProperties());
    }

//    @Bean // Needed by org.springdoc:springdoc-openapi-ui:1.5.13
//    public OpenAPIService objectMapperProvider() {
//        return new OpenAPIService();
//    }
}
