package com.smartconsultor.microservice.business.domain.service;

import com.smartconsultor.microservice.business.domain.model.Order;
import com.smartconsultor.microservice.business.domain.model.User;

public class OrderService {
    public String generateLog(Order order, User user) {
        return String.format("Order %s by %s (%s): %d x %d",
                order.orderId, user.fullName, user.email,
                order.quantity, order.price);
    }
}
