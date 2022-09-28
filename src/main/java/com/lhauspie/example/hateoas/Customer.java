package com.lhauspie.example.hateoas;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.UUID;

@Data
@Builder
@Relation(collectionRelation = "customers", itemRelation = "customer")
public class Customer extends RepresentationModel<Customer> {
    private UUID customerId;
    private String customerName;
    private String companyName;
}