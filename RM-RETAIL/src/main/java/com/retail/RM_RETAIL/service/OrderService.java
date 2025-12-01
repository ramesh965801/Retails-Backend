package com.retail.RM_RETAIL.service;

import com.retail.RM_RETAIL.entity.Order;
import com.retail.RM_RETAIL.entity.OrderItem;
import com.retail.RM_RETAIL.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository) { this.orderRepository = orderRepository; }

    public Order saveOrder(Order order) {
        if (order.getOrderDate() == null) order.setOrderDate(LocalDate.now());
        if (order.getOrderTime() == null) order.setOrderTime(LocalTime.now());

        if (order.getOrderItems() == null) order.setOrderItems(new ArrayList<>());
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(i -> i.setOrder(order));
        }


        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(order); // link item to order
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() { return orderRepository.findAll(); }

    public Order getOrderById(Long id) { return orderRepository.findById(id).orElse(null); }

    public List<Order> getRecentOrders() { return orderRepository.findTop5ByOrderByIdDesc(); }

    public double getTodaySales(LocalDate date) {
        return orderRepository.findByOrderDate(date)
                .stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public long getTodayOrdersCount(LocalDate date) {
        return orderRepository.findByOrderDate(date).size();
    }
}
