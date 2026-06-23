package com.laundrypos.modules.sales.repository;

import com.laundrypos.modules.sales.model.ServiceOrder;
import com.laundrypos.modules.sales.model.ServiceOrder.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByStatusNot(OrderStatus status);
    List<ServiceOrder> findByStatus(OrderStatus status);
    List<ServiceOrder> findByClientId(Long clientId);
    List<ServiceOrder> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
