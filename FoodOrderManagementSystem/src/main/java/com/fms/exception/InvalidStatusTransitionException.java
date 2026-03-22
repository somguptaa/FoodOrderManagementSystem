package com.fms.exception;

import com.fms.enums.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Cannot transition order status from " + from + " to " + to);
    }
}