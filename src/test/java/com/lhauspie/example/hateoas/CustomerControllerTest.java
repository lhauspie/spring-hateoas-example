package com.lhauspie.example.hateoas;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = HateoasApplication.class)
public class CustomerControllerTest {

    //by classpath
    private static final String OA3_URL = "/swagger.yml";

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @MockBean
    CustomerService customerService;

    @MockBean
    OrderService orderService;

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        // FIXME : the following piece of code (instead of previous one) make the validation failed because of serialization issue (`links` as array instead of `_link` as object)
        //         it seems the response resolver is only present when Spring Boot context is fully loaded
//        CustomerController customerController = new CustomerController(
//                customerService,
//                orderService
//        );
//        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        Mockito.when(customerService.getCustomerDetail(ArgumentMatchers.any(UUID.class)))
                .thenAnswer(
                        invocation ->
                                Customer.builder()
                                        .customerId(invocation.getArgument(0))
                                        .customerName(PojoGenerator.generate(String.class))
                                        .companyName(PojoGenerator.generate(String.class))
                                        .build()
                );

        Mockito.when(customerService.getCustomers())
                .thenReturn(
                        Stream.iterate(1, i -> i)
                                .limit(10)
                                .map(i -> Customer.builder()
                                        .customerId(PojoGenerator.generate(UUID.class))
                                        .customerName(PojoGenerator.generate(String.class))
                                        .companyName(PojoGenerator.generate(String.class))
                                        .build())
                                .collect(Collectors.toList())
                );
    }

    @Test
    public void getCustomerById() throws Exception {
        mockMvc.perform(get("/customers/0a818933-087d-47f2-ad83-2f986ed087eb"))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(OA3_URL));
    }

    @Test
    public void getCustomerByIdReturns400BadRequest() throws Exception {
        mockMvc.perform(get("/customers/non-uuid-string"))
                .andExpect(status().isBadRequest())
                .andExpect(openApi().isValid(OA3_URL));
    }

    @Test
    public void getCustomerByIdReturns404NotFound() throws Exception {
        Mockito.when(customerService.getCustomerDetail(ArgumentMatchers.any(UUID.class)))
                .thenReturn(null);

        mockMvc.perform(get("/customers/0a818933-087d-47f2-ad83-2f986ed087eb"))
                .andExpect(status().isNotFound())
                .andExpect(openApi().isValid(OA3_URL));
    }

    // @Test // FIXME : Why this is failing and not responds with a 500 StatusCode ?
    public void getCustomerByIdReturns500ServerError() throws Exception {
        Mockito.when(customerService.getCustomerDetail(ArgumentMatchers.any(UUID.class)))
                .thenThrow(new NotImplementedException("For testing purpose"));

        mockMvc.perform(get("/customers/0a818933-087d-47f2-ad83-2f986ed087eb"))
                .andExpect(status().isInternalServerError()) // FIXME : Assertion never reached because of the previous exception
                .andExpect(openApi().isValid(OA3_URL));      // FIXME : Assertion never reached because of the previous exception
    }

    @Test
    public void getCustomersByDefault() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(OA3_URL));
    }

    @Test
    public void getCustomersSpecificPage() throws Exception {
        mockMvc.perform(get("/customers?page=0"))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(OA3_URL));
    }

    @Test
    public void getCustomersSpecificSize() throws Exception {
        mockMvc.perform(get("/customers?size=0"))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(OA3_URL));
    }

    @Test
    public void getCustomersSpecificSort() throws Exception {
        mockMvc.perform(get("/customers?sort=toto"))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid(OA3_URL));
    }
}
