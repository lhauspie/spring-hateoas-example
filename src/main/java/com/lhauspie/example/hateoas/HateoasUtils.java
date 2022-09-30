package com.lhauspie.example.hateoas;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class HateoasUtils {

    public static <T> PagedModel<T> toPagedModel(
            @NotNull List<T> items,
            @NotNull PageMetadata currentPage,
            @NotNull BiFunction<Long, Long, WebMvcLinkBuilder> function,
            Link... additionnalLinks) {
        List<Link> paginationLinks = new ArrayList<>(5);

        paginationLinks.add(function.apply(0L, currentPage.getSize()).withRel(IanaLinkRelations.FIRST));
        if (currentPage.getNumber() > 0) {
            paginationLinks.add(function.apply(currentPage.getNumber() - 1, currentPage.getSize()).withRel(IanaLinkRelations.PREVIOUS));
        }
        // `.expand()` avoid having a `self` link to be templated.
        paginationLinks.add(function.apply(currentPage.getNumber(), currentPage.getSize()).withSelfRel().expand());
        if (currentPage.getNumber() < currentPage.getTotalPages() - 1) {
            paginationLinks.add(function.apply(currentPage.getNumber() + 1, currentPage.getSize()).withRel(IanaLinkRelations.NEXT));
        }
        if (currentPage.getTotalPages() > 0) {
            paginationLinks.add(function.apply(currentPage.getTotalPages() - 1, currentPage.getSize()).withRel(IanaLinkRelations.LAST));
        }
        paginationLinks.addAll(Arrays.stream(additionnalLinks).toList());

        return PagedModel.of(items, currentPage, paginationLinks);
    }
}
