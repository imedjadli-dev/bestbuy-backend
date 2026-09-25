package com.bestbuy.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(
            String productName, Integer requested,
            Integer available
    ) {
        super(String.format("Insufficient stock for product '%s'. Requested: " +
                "%d , available: %d", productName, requested, available));
    }
}
