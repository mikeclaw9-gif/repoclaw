package com.laundrypos.modules.sales.repository;

import com.laundrypos.modules.sales.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
