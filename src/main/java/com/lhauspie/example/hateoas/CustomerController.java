package com.lhauspie.example.hateoas;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping(value = "/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final OrderService orderService;

    static final LinkRelation ORDERS = LinkRelation.of("orders");

    @GetMapping(value = "", produces = "application/hal+json")
    public ResponseEntity<PagedModel<Customer>> getCustomers(
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Long page,
            @Min(1) @RequestParam(required = false, defaultValue = "100") Long size,
            @RequestParam(required = false) Optional<List<String>> sort,
            @RequestParam(required = false) Optional<List<String>> fields // FIXME: How to implement this ?
    ) {
        List<Customer> customers = customerService.getCustomers()
                .stream().map(customer -> customer
                        .add(linkTo(methodOn(CustomerController.class).getCustomerById(customer.getCustomerId())).withSelfRel().expand())
                        .add(linkTo(methodOn(CustomerController.class).getOrdersForCustomer(customer.getCustomerId(), null, null, Optional.empty(), Optional.empty())).withRel(ORDERS))
                )
                .collect(Collectors.toList());
        long totalElements = 1000L; // TODO : this information should come from the service layer

        return ResponseEntity.ok(
                HateoasUtils.toPagedModel(
                        customers,
                        new PageMetadata(size, page, totalElements),
                        (aPage, aSize) -> linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomers(aPage, aSize, sort, fields))
                )
        );
    }

    @GetMapping(value = "/{customerId}", produces = "application/hal+json")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID customerId) {
        Customer customer = customerService.getCustomerDetail(customerId);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        customer.add(
                linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel().expand(),
                linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, null, null, Optional.empty(), Optional.empty())).withRel(ORDERS)
        );
        return ResponseEntity.ok(customer);
    }


    @GetMapping(value = "/{customerId}/orders", produces = {"application/hal+json"})
    public ResponseEntity<PagedModel<Order>> getOrdersForCustomer(
            @PathVariable final UUID customerId,
            @RequestParam(required = false, defaultValue = "0") Long page,
            @RequestParam(required = false, defaultValue = "100") Long size,
            @RequestParam(required = false) Optional<List<String>> sort,
            @RequestParam(required = false) Optional<List<String>> fields // FIXME: How to implement this ?
    ) {
        List<Order> orders = orderService.getAllOrdersForCustomer(customerId)
                .stream().map(order -> order
                        .add(linkTo(methodOn(CustomerController.class).getOrderById(customerId, order.getOrderId())).withSelfRel().expand())
                )
                .collect(Collectors.toList());
        long totalElements = 100L; // TODO : this information should come from the service layer

        return ResponseEntity.ok(
                HateoasUtils.toPagedModel(
                        orders,
                        new PageMetadata(size, page, totalElements),
                        (aPage, aSize) -> linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, aPage, aSize, sort, fields))
                )
        );
    }

    @GetMapping(value = "/{customerId}/orders/{orderId}", produces = {"application/hal+json"})
    public Order getOrderById(@PathVariable final UUID customerId, @PathVariable final String orderId) {
        Order order = orderService.getOrder(customerId, orderId);
        order.add(
                linkTo(methodOn(CustomerController.class).getOrderById(customerId, orderId)).withSelfRel()
        );
        return order;
    }
}