package com.laundrypos.modules.sales.repository;

import com.laundrypos.modules.sales.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}
