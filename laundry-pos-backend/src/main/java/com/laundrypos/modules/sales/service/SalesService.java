package com.laundrypos.modules.sales.service;

import com.laundrypos.modules.auth.model.User;
import com.laundrypos.modules.auth.repository.UserRepository;
import com.laundrypos.modules.clients.model.Client;
import com.laundrypos.modules.clients.repository.ClientRepository;
import com.laundrypos.modules.inventory.model.Product;
import com.laundrypos.modules.inventory.repository.ProductRepository;
import com.laundrypos.modules.sales.dto.OrderStatusRequest;
import com.laundrypos.modules.sales.dto.ServiceOrderRequest;
import com.laundrypos.modules.sales.dto.ServiceOrderResponse;
import com.laundrypos.modules.sales.model.*;
import com.laundrypos.modules.sales.repository.*;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final ServiceOrderRepository orderRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Transactional
    public ServiceOrderResponse createOrder(ServiceOrderRequest request) {
        var user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.userId()));

        Client client = null;
        if (request.clientId() != null) {
            client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.clientId()));
        }

        var order = ServiceOrder.builder()
            .client(client)
            .user(user)
            .status(ServiceOrder.OrderStatus.PENDIENTE)
            .build();

        double totalAmount = 0;
        double totalWeight = 0;

        for (var itemReq : request.items()) {
            var serviceType = serviceTypeRepository.findById(itemReq.serviceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de servicio", itemReq.serviceTypeId()));

            double price = serviceType.getPricePerKg() * itemReq.weight();
            totalAmount += price;
            totalWeight += itemReq.weight();

            var item = OrderItem.builder()
                .order(order)
                .serviceType(serviceType)
                .weight(itemReq.weight())
                .price(price)
                .build();
            order.getItems().add(item);
        }

        if (request.products() != null) {
            for (var prodReq : request.products()) {
                var product = productRepository.findById(prodReq.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", prodReq.productId()));

                if (product.getStock() < prodReq.quantity()) {
                    throw new IllegalArgumentException("Stock insuficiente para: " + product.getName());
                }

                double subtotal = product.getPrice() * prodReq.quantity();
                totalAmount += subtotal;

                product.setStock(product.getStock() - prodReq.quantity());
                productRepository.save(product);

                var orderProduct = OrderProduct.builder()
                    .order(order)
                    .product(product)
                    .quantity(prodReq.quantity())
                    .unitPrice(product.getPrice())
                    .build();
                order.getOrderProducts().add(orderProduct);
            }
        }

        order.setTotalAmount(totalAmount);
        order.setTotalWeight(totalWeight);

        return toResponse(orderRepository.save(order));
    }

    public List<ServiceOrderResponse> getActiveOrders() {
        return orderRepository.findByStatusNot(ServiceOrder.OrderStatus.ENTREGADO).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<ServiceOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    public ServiceOrderResponse getOrder(Long id) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orden", id));
        return toResponse(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orden", id));
        orderRepository.delete(order);
    }

    @Transactional
    public ServiceOrderResponse updateStatus(Long id, OrderStatusRequest request) {
        var order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Orden", id));

        var newStatus = ServiceOrder.OrderStatus.valueOf(request.status());
        order.setStatus(newStatus);

        if (newStatus == ServiceOrder.OrderStatus.LISTO) {
            order.setReadyAt(LocalDateTime.now());
        }

        return toResponse(orderRepository.save(order));
    }

    private ServiceOrderResponse toResponse(ServiceOrder order) {
        return new ServiceOrderResponse(
            order.getId(),
            order.getClient() != null ? order.getClient().getId() : null,
            order.getClient() != null ? order.getClient().getName() : null,
            order.getUser().getId(),
            order.getUser().getUsername(),
            order.getTotalWeight(),
            order.getTotalAmount(),
            order.getStatus().name(),
            order.getCreatedAt(),
            order.getReadyAt(),
            order.getItems().stream().map(item -> new ServiceOrderResponse.OrderItemResponse(
                item.getId(),
                item.getServiceType().getName(),
                item.getWeight(),
                item.getPrice()
            )).toList(),
            order.getOrderProducts().stream().map(op -> new ServiceOrderResponse.OrderProductResponse(
                op.getId(),
                op.getProduct().getName(),
                op.getQuantity(),
                op.getUnitPrice(),
                op.getQuantity() * op.getUnitPrice()
            )).toList()
        );
    }
}
