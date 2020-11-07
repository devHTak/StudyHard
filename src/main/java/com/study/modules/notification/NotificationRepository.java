package com.study.modules.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.modules.account.Account;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
	long countByAccountAndChecked(Account account, boolean checked);
	
	List<Notification> findByNotificationType(NotificationType notificationType);
	
}
