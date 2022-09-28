package com.lhauspie.example.hateoas.swagger;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/v3")
public class SwaggerController {

    /**
     * This allows to override the springdoc-generated swagger.json
     *
     * @return the content of the `/src/main/resources/swagger.yml` file
     * @throws IOException in case the file cannot be read for any reason
     */
    @GetMapping(value = "/api-docs", produces = { MediaType.TEXT_PLAIN_VALUE })
    public ResponseEntity<byte[]> getSwagger() throws IOException, URISyntaxException {
        return ResponseEntity.ok(
                SwaggerController.class.getResourceAsStream("/swagger.yml").readAllBytes()
        );

    }
}