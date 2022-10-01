package com.lhauspie.example.hateoas;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.List;
import java.util.Optional;

/**
 * Class just to not loose the type of return objects in controller methods.
 *
 * @param <T> the type of the value inside MappginJacksonValue.
 */
public class MappingJacksonValue<T> extends org.springframework.http.converter.json.MappingJacksonValue {

    public static final String FIELDS_FILTER = "FieldsFilter";

    /**
     * Create a new instance wrapping the given POJO to be serialized.
     *
     * @param value the Object to be serialized
     */
    public MappingJacksonValue(T value) {
        this(value, Optional.empty());
    }

    /**
     * Create a new instance wrapping the given POJO to be serialized.
     *
     * @param value the Object to be serialized
     * @param fields the fields to include in the response
     */
    public MappingJacksonValue(T value, Optional<List<String>> fields) {
        super(value);

        SimpleBeanPropertyFilter filter = fields
                .map(theFields -> {
                    theFields.add("_links"); // _links is the default name of links properties from HATEOAS
                    return SimpleBeanPropertyFilter.filterOutAllExcept(theFields.toArray(new String[0]));
                })
                .orElseGet(() -> SimpleBeanPropertyFilter.serializeAll());

        setFilters(
                new SimpleFilterProvider().addFilter(MappingJacksonValue.FIELDS_FILTER, filter)
        );
    }

    public static <T> MappingJacksonValue<T> of(T value) {
        return new MappingJacksonValue<>(value);
    }

    public static <T> MappingJacksonValue<T> of(T value, Optional<List<String>> fields) {
        return new MappingJacksonValue<>(value, fields);
    }
}
