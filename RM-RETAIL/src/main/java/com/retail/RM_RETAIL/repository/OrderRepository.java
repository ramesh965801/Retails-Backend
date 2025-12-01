package com.retail.RM_RETAIL.repository;

import com.retail.RM_RETAIL.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findTop5ByOrderByIdDesc();
    List<Order> findByOrderDate(LocalDate date);
}
