package com.retail.RM_RETAIL.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.retail.RM_RETAIL.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
