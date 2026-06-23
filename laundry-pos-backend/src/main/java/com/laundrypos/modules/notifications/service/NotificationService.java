package com.laundrypos.modules.notifications.service;

import com.laundrypos.modules.notifications.dto.NotificationTemplateRequest;
import com.laundrypos.modules.notifications.model.Notification;
import com.laundrypos.modules.notifications.repository.NotificationRepository;
import com.laundrypos.modules.sales.model.ServiceOrder;
import com.laundrypos.modules.sales.repository.ServiceOrderRepository;
import com.laundrypos.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ServiceOrderRepository orderRepository;

    public void sendReadyNotification(Long orderId) {
        var order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Orden", orderId));

        if (order.getClient() == null || order.getClient().getPhone() == null) {
            throw new IllegalArgumentException("La orden no tiene cliente o teléfono asociado");
        }

        var notification = Notification.builder()
            .client(order.getClient())
            .order(order)
            .type("WHATSAPP")
            .status("PENDING")
            .build();

        notificationRepository.save(notification);
    }

    public List<String> getTemplates() {
        return List.of(
            "Su servicio de lavandería #%d está LISTO para recoger.",
            "¡Hola %s! Su orden #%d ya puede pasar a recogerla. Gracias por su preferencia."
        );
    }

    public void updateTemplate(Long id, NotificationTemplateRequest request) {
    }
}
