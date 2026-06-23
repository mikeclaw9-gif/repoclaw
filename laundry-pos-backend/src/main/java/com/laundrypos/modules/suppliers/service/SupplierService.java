package com.laundrypos.modules.suppliers.service;

import com.laundrypos.modules.inventory.model.Product;
import com.laundrypos.modules.inventory.repository.ProductRepository;
import com.laundrypos.modules.suppliers.dto.SupplierRequest;
import com.laundrypos.modules.suppliers.dto.SupplierResponse;
import com.laundrypos.modules.suppliers.model.Supplier;
import com.laundrypos.modules.suppliers.repository.SupplierRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public List<SupplierResponse> findAll() {
        return supplierRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public SupplierResponse create(SupplierRequest request) {
        var supplier = Supplier.builder()
            .name(request.name())
            .contactName(request.contactName())
            .phone(request.phone())
            .email(request.email())
            .address(request.address())
            .build();
        return toResponse(supplierRepository.save(supplier));
    }

    public SupplierResponse update(Long id, SupplierRequest request) {
        var supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));

        supplier.setName(request.name());
        supplier.setContactName(request.contactName());
        supplier.setPhone(request.phone());
        supplier.setEmail(request.email());
        supplier.setAddress(request.address());

        return toResponse(supplierRepository.save(supplier));
    }

    public List<Product> getPurchaseHistory(Long supplierId) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Proveedor", supplierId);
        }
        return productRepository.findByCategory("").stream()
            .filter(p -> p.getSupplier() != null && p.getSupplier().getId().equals(supplierId))
            .toList();
    }

    private SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(
            supplier.getId(), supplier.getName(), supplier.getContactName(),
            supplier.getPhone(), supplier.getEmail(), supplier.getAddress());
    }
}
