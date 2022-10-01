package com.lhauspie.example.hateoas;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@Relation(collectionRelation = "orders", itemRelation = "order")
@JsonFilter(MappingJacksonValue.FIELDS_FILTER)
public class Order extends RepresentationModel<Order> {
    private String orderId;
    private double price;
    private int quantity;
}