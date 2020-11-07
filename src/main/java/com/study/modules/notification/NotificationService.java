package com.study.modules.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
	
	private final NotificationRepository notificationRepository;
	
	public Notification saveNotification(Notification notification) {
		return notificationRepository.save(notification);
	}
	
	public long countByAccountAndChecked(Account account, boolean checked) {
		return notificationRepository.countByAccountAndChecked(account, checked);
	}
	
	public List<Notification> findByNotificationType(NotificationType notificationType) {
		return notificationRepository.findByNotificationType(notificationType);
	}

}
