package com.laundrypos.modules.suppliers.repository;

import com.laundrypos.modules.suppliers.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
