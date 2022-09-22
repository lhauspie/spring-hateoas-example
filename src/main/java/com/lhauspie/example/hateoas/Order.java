package com.lhauspie.example.hateoas;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@Relation(collectionRelation = "orders", itemRelation = "order")
public class Order extends RepresentationModel<Order> {
    private String orderId;
    private double price;
    private int quantity;
}