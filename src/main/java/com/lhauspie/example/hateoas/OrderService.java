package com.lhauspie.example.hateoas;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrderService {

    public List<Order> getAllOrdersForCustomer(UUID customerId) {
        return Stream.iterate(1, i -> i)
                .limit(10)
                .map(integer -> Order.builder()
                        .orderId(PojoGenerator.generate(String.class))
                        .price(PojoGenerator.generate(Double.class))
                        .quantity(PojoGenerator.generate(Integer.class))
                        .build())
                .collect(Collectors.toList());
    }

    public Order getOrder(UUID customerId, String orderId) {
        return Order.builder()
                .orderId(orderId)
                .price(PojoGenerator.generate(Double.class))
                .quantity(PojoGenerator.generate(Integer.class))
                .build();
    }
}
