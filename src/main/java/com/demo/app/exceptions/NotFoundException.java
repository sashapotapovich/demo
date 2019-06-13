package com.demo.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    private String property;
    private Integer value;
    private String table;

    public NotFoundException(String property, Integer value, String table) {
        super(String.format("The item with %s equal to %d doesn't exist in %s.", property, value, table));
        this.property = property;
        this.value = value;
        this.table = table;
    }

    public String getProperty() { return property; }
    public Integer getValue() { return value; }
    public String getTable() { return table; }
}
