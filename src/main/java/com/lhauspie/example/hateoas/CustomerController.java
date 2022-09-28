package com.lhauspie.example.hateoas;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final OrderService orderService;

    static final LinkRelation ORDERS = LinkRelation.of("orders");

    @GetMapping(value = "", produces = "application/hal+json")
    public ResponseEntity<PagedModel<Customer>> getCustomers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer size,
            @RequestParam(required = false) Optional<List<String>> sort,
            @RequestParam(required = false) Optional<List<String>> fields // FIXME: How to implement this ?
    ) throws NoSuchMethodException {
        List<Customer> customers = customerService.getCustomers()
                .stream().map(customer -> customer
                        .add(WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class).getCustomerById(customer.getCustomerId())).withSelfRel().expand())
                        .add(WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customer.getCustomerId(), null, null, null, null)).withRel(ORDERS))
                )
                .collect(Collectors.toList());
        int totalElements = 1000; // TODO : this information should come from the service layer

        // FIXME : Find a way to avoid to duplicate this piece of code =================================================
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) size);
        List<Link> paginationLinks = new ArrayList<>(5);

        paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomers(0, size, sort, fields)).withRel(IanaLinkRelations.FIRST));
        if (page > 0) {
            paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomers(page - 1, size, sort, fields)).withRel(IanaLinkRelations.PREVIOUS));
        }
        // `.expand()` avoid having a `self` link to be templated.
        paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomers(page, size, sort, fields)).withSelfRel().expand());
        if (page < totalPages - 1) {
            paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomers(page + 1, size, sort, fields)).withRel(IanaLinkRelations.NEXT));
        }
        paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getCustomers(totalPages - 1, size, sort, fields)).withRel(IanaLinkRelations.LAST));

        return ResponseEntity.ok(
                PagedModel.of(
                        customers,
                        new PagedModel.PageMetadata(size, page, totalElements, totalPages),
                        paginationLinks
                )
        );
        //==============================================================================================================
    }

    @GetMapping(value = "/{customerId}", produces = "application/hal+json")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID customerId) {
        Customer customer = customerService.getCustomerDetail(customerId);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        customer.add(
                WebMvcLinkBuilder.linkTo(CustomerController.class).slash(customer.getCustomerId()).withSelfRel().expand(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, null, null, null, null)).withRel(ORDERS)
        );
        return ResponseEntity.ok(customer);
    }


    @GetMapping(value = "/{customerId}/orders", produces = {"application/hal+json"})
    public ResponseEntity<PagedModel<Order>> getOrdersForCustomer(
            @PathVariable final UUID customerId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer size,
            @RequestParam(required = false) Optional<String[]> sort,
            @RequestParam(required = false) Optional<String[]> fields // FIXME: How to implement this ?
    ) {
        List<Order> orders = orderService.getAllOrdersForCustomer(customerId)
                .stream().map(order -> order
                        .add(WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class).getOrderById(customerId, order.getOrderId())).withSelfRel().expand())
                )
                .collect(Collectors.toList());
        int totalElements = 100; // TODO : this information should come from the service layer

        // FIXME : Find a way to avoid to duplicate this piece of code =================================================
        int totalPages = size == 0 ? 1 : (int) Math.ceil((double) totalElements / (double) size);
        List<Link> paginationLinks = new ArrayList<>(5);
        paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, 0, size, sort, fields)).withRel(IanaLinkRelations.FIRST));
        if (page > 0) {
            paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, page - 1, size, sort, fields)).withRel(IanaLinkRelations.PREVIOUS));
        }
        paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, page, size, sort, fields)).withSelfRel().expand());
        if (page < totalPages - 1) {
            paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, page + 1, size, sort,fields)).withRel(IanaLinkRelations.NEXT));
        }
        paginationLinks.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CustomerController.class).getOrdersForCustomer(customerId, totalPages - 1, size, sort, fields)).withRel(IanaLinkRelations.LAST));

        return ResponseEntity.ok(
                PagedModel.of(
                        orders,
                        new PagedModel.PageMetadata(size, page, totalElements, totalPages),
                        paginationLinks
                )
        );
        //==============================================================================================================
    }

    @GetMapping(value = "/{customerId}/orders/{orderId}", produces = {"application/hal+json"})
    public Order getOrderById(@PathVariable final UUID customerId, @PathVariable final String orderId) {
        Order order = orderService.getOrder(customerId, orderId);
        order.add(
                WebMvcLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(CustomerController.class).getOrderById(customerId, orderId)).withSelfRel()
        );
        return order;
    }
}