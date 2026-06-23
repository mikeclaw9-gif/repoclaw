package com.laundrypos.modules.services.repository;

import com.laundrypos.modules.services.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    boolean existsByName(String name);
    Optional<ServiceEntity> findByName(String name);
    List<ServiceEntity> findByActiveTrue();
}
