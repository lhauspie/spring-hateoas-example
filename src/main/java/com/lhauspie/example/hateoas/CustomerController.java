package com.lhauspie.example.hateoas;

import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PagedResourcesAssembler<Customer> pagedCustomerResourcesAssembler;

    @Autowired
    private PagedResourcesAssembler<Order> pagedOrderResourcesAssembler;

    @GetMapping("/A")
    @PageableAsQueryParam
    public ResponseEntity<PagedModel<Customer>> getCustomers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "") String[] sort
    ) {
//        List<Customer> customers = customerService.getCustomers();
//        for (final Customer customer : customers) {
//            Link selfLink = WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class)
//                    .getCustomerById(customer.getCustomerId())).withSelfRel();
//            customer.add(selfLink);
//        }
//
//        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class)
//                .getCustomers(page, size, sort, pagedResourcesAssembler)).withSelfRel();
//        CollectionModel<Customer> result = CollectionModel.of(customers, selfLink);

        List<Customer> customers = customerService.getCustomers();
        return ResponseEntity.ok(
                pagedCustomerResourcesAssembler.toModel(
                        new PageImpl<>(customers, PageRequest.of(page, size, Sort.by(sort)), 1000),
                        customer -> customer.add(WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class)
                            .getCustomerById(customer.getCustomerId())).withSelfRel())
                )
        );
    }

    @GetMapping("/B")
    public ResponseEntity<PagedModel<Customer>> getCustomers(
            @PageableDefault Pageable pageRequest
    ) {
        List<Customer> customers = customerService.getCustomers();
        return ResponseEntity.ok(
                pagedCustomerResourcesAssembler.toModel(
                        new PageImpl<>(customers, pageRequest, 1000),
                        customer -> customer.add(WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class)
                            .getCustomerById(customer.getCustomerId())).withSelfRel())
                )
        );
    }

    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable String customerId) {
        Customer customer = customerService.getCustomerDetail(customerId);
        customer.add(
                WebMvcLinkBuilder.linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class)
                        .getOrdersForCustomer(customerId, 0, 20, null))
                        .withRel("orders")
        );
        return customer;
    }


    @GetMapping(value = "/{customerId}/orders", produces = { "application/hal+json" })
    @PageableAsQueryParam
    public ResponseEntity<PagedModel<Order>> getOrdersForCustomer(
            @PathVariable final String customerId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String[] sort
    ) {
        List<Order> orders = orderService.getAllOrdersForCustomer(customerId);
        for (final Order order : orders) {
            Link selfLink = WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class)
                    .getOrderById(customerId, order.getOrderId())).withSelfRel();
            order.add(selfLink);
        }

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class)
                .getOrdersForCustomer(customerId, page, size, sort)).withSelfRel();

        return ResponseEntity.ok(
                pagedOrderResourcesAssembler
                        .toModel(
                                new PageImpl<>(orders, PageRequest.of(page, size), 20),
                                order -> order.add(WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class)
                                        .getOrderById(customerId, order.getOrderId())).withSelfRel()),
                                selfLink)
        );
    }

    @GetMapping(value = "/{customerId}/orders/{orderId}", produces = { "application/hal+json" })
    public Order getOrderById(@PathVariable final String customerId, @PathVariable final String orderId) {
        Order order = orderService.getOrder(customerId, orderId);
        order.add(
                WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class).getOrderById(customerId, orderId)).withSelfRel()
        );
        return order;
    }
}