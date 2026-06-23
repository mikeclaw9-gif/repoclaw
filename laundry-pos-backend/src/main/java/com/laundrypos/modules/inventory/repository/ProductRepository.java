package com.laundrypos.modules.inventory.repository;

import com.laundrypos.modules.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStockLessThanEqual(int minStock);
    List<Product> findByCategory(String category);
}
