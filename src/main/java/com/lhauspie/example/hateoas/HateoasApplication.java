package com.lhauspie.example.hateoas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class HateoasApplication {

  public static void main(String[] args) {
    SpringApplication.run(HateoasApplication.class, args);
  }
}
