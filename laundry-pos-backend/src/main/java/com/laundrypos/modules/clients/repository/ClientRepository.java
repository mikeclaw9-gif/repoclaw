package com.laundrypos.modules.clients.repository;

import com.laundrypos.modules.clients.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCase(String name);
    List<Client> findByPhoneContaining(String phone);
    List<Client> findByEmailContainingIgnoreCase(String email);
}
