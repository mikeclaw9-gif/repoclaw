package com.laundrypos.modules.notifications.repository;

import com.laundrypos.modules.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
