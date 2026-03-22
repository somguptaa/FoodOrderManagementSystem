package com.fms.enums;

public enum OrderStatus {

    PENDING,      // Order placed, awaiting confirmation
    CONFIRMED,    // Restaurant confirmed the order
    PREPARING,    // Kitchen is preparing the order
    DELIVERED,    // Order delivered to customer
    CANCELLED;    // Order cancelled (from any state)

    // Valid transitions — defines what state can follow the current one
    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING    -> next == CONFIRMED  || next == CANCELLED;
            case CONFIRMED  -> next == PREPARING  || next == CANCELLED;
            case PREPARING  -> next == DELIVERED  || next == CANCELLED;
            case DELIVERED  -> false; // terminal state
            case CANCELLED  -> false; // terminal state
        };
    }
}