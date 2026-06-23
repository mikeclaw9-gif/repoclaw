package com.laundrypos.modules.sales.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.laundrypos.modules.sales.dto.TicketResponse;
import com.laundrypos.modules.sales.model.ServiceOrder;
import com.laundrypos.modules.sales.repository.ServiceOrderRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final ServiceOrderRepository orderRepository;

    public TicketResponse generateTicket(Long orderId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Orden", orderId));

        var barcode = generateBarcode(order.getId());

        return toTicketResponse(order, barcode);
    }

    private String generateBarcode(Long id) {
        try {
            var writer = new Code128Writer();
            var bitMatrix = writer.encode(String.valueOf(id), BarcodeFormat.CODE_128, 400, 100);
            var bos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", bos);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar código de barras", e);
        }
    }

    private TicketResponse toTicketResponse(ServiceOrder order, String barcode) {
        return new TicketResponse(
            order.getId(),
            order.getClient() != null ? order.getClient().getName() : null,
            order.getUser().getUsername(),
            order.getTotalWeight(),
            order.getTotalAmount(),
            order.getStatus().name(),
            order.getCreatedAt(),
            order.getItems().stream().map(item -> new TicketResponse.TicketItemResponse(
                item.getServiceType().getName(),
                item.getWeight(),
                item.getPrice()
            )).toList(),
            order.getOrderProducts().stream().map(op -> new TicketResponse.TicketProductResponse(
                op.getProduct().getName(),
                op.getQuantity(),
                op.getUnitPrice(),
                op.getQuantity() * op.getUnitPrice()
            )).toList(),
            barcode
        );
    }
}
