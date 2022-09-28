package com.lhauspie.example.hateoas;

import com.lhauspie.example.hateoas.swagger.SwaggerController;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HateoasApplication.class)
public class RestApiSchemaIntegrationTest {

    String swaggerContent;

    @BeforeEach
    public void setup() throws URISyntaxException, IOException {
        swaggerContent = Files.readString(Paths.get(getClass().getResource("/swagger.yml").toURI()), Charset.forName("utf-8"));
    }

    @Test
    public void validateThatImplementationMatchesDocumentationSpecification_THIS_IS_WORKING() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new SwaggerController())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(result ->
                    MatcherAssert.assertThat(
                            result.getResponse().getContentAsString(),
                            Is.is(swaggerContent)
                    )
                );
    }

    @Autowired
    WebApplicationContext context;

    @Test
    public void validateThatImplementationMatchesDocumentationSpecification_THIS_IS_WORKING_TOO() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(result ->
                    MatcherAssert.assertThat(
                            result.getResponse().getContentAsString(),
                            Is.is(swaggerContent)
                    )
                );
    }

    @LocalServerPort
    int port;

    // @Test // FIXME : Why this is failing with a 400 Bad Request response ?
    public void validateThatImplementationMatchesDocumentationSpecification_BUT_WHY_THIS_IS_NOT_WORKING() throws Exception {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(String.format("http://localhost:%d/", port))
                .build();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/v3/api-docs", String.class);
        MatcherAssert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
        MatcherAssert.assertThat(responseEntity.getBody(), Is.is(swaggerContent));
    }

    @Test
    public void validateThatImplementationMatchesDocumentationSpecification_WHILE_THIS_IS_WORKING() throws Exception {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(String.format("http://localhost:%d/", port))
                .build();

        ResponseEntity<String> responseEntity = restTemplate.getForEntity("/customers/0a818933-087d-47f2-ad83-2f986ed087eb", String.class);
        MatcherAssert.assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.OK));
        MatcherAssert.assertThat(responseEntity.getBody(), StringContains.containsString("\"customerId\":\"0a818933-087d-47f2-ad83-2f986ed087eb\""));
    }
}