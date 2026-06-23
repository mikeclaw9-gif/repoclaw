package com.laundrypos.modules.inventory.service;

import com.laundrypos.modules.inventory.dto.ProductRequest;
import com.laundrypos.modules.inventory.dto.ProductResponse;
import com.laundrypos.modules.inventory.dto.StockMovementRequest;
import com.laundrypos.modules.inventory.model.InventoryMovement;
import com.laundrypos.modules.inventory.model.Product;
import com.laundrypos.modules.inventory.repository.InventoryMovementRepository;
import com.laundrypos.modules.inventory.repository.ProductRepository;
import com.laundrypos.modules.suppliers.repository.SupplierRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;
    private final SupplierRepository supplierRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ProductResponse createProduct(ProductRequest request) {
        var supplier = request.supplierId() != null
            ? supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.supplierId()))
            : null;

        var product = Product.builder()
            .name(request.name())
            .category(request.category())
            .price(request.price())
            .stock(request.stock())
            .minStock(request.minStock())
            .supplier(supplier)
            .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        var product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto", id));

        var supplier = request.supplierId() != null
            ? supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", request.supplierId()))
            : null;

        product.setName(request.name());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setMinStock(request.minStock());
        product.setSupplier(supplier);

        return toResponse(productRepository.save(product));
    }

    public List<InventoryMovement> getStockMovements() {
        return movementRepository.findAll();
    }

    @Transactional
    public InventoryMovement stockEntry(StockMovementRequest request) {
        var product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto", request.productId()));

        product.setStock(product.getStock() + request.quantity());
        productRepository.save(product);

        var movement = InventoryMovement.builder()
            .product(product)
            .type(InventoryMovement.MovementType.ENTRADA)
            .quantity(request.quantity())
            .reason(request.reason())
            .build();

        return movementRepository.save(movement);
    }

    @Transactional
    public InventoryMovement stockExit(StockMovementRequest request) {
        var product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto", request.productId()));

        if (product.getStock() < request.quantity()) {
            throw new IllegalArgumentException("Stock insuficiente");
        }

        product.setStock(product.getStock() - request.quantity());
        productRepository.save(product);

        var movement = InventoryMovement.builder()
            .product(product)
            .type(InventoryMovement.MovementType.SALIDA)
            .quantity(request.quantity())
            .reason(request.reason())
            .build();

        return movementRepository.save(movement);
    }

    public List<ProductResponse> getLowStockAlerts() {
        return productRepository.findAll().stream()
            .filter(p -> p.getStock() <= p.getMinStock())
            .map(this::toResponse)
            .toList();
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(), product.getName(), product.getCategory(),
            product.getPrice(), product.getStock(), product.getMinStock(),
            product.getSupplier() != null ? product.getSupplier().getId() : null,
            product.getSupplier() != null ? product.getSupplier().getName() : null,
            product.getStock() <= product.getMinStock());
    }
}
