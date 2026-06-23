package com.laundrypos.modules.sales.repository;

import com.laundrypos.modules.sales.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
