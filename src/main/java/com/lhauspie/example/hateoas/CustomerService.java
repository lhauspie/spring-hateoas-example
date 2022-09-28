package com.lhauspie.example.hateoas;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerService {

    public List<Customer> getCustomers() {
        return Stream.iterate(1, i -> i)
                .limit(10)
                .map(i -> Customer.builder()
                        .customerId(PojoGenerator.generate(UUID.class))
                        .customerName(PojoGenerator.generate(String.class))
                        .companyName(PojoGenerator.generate(String.class))
                        .build())
                .collect(Collectors.toList());
    }

    public Customer getCustomerDetail(UUID customerId) {
        return Customer.builder()
                .customerId(customerId)
                .customerName(PojoGenerator.generate(String.class))
                .companyName(PojoGenerator.generate(String.class))
                .build();
    }
}
