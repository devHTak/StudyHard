package com.study.modules.event.event;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.event.Event;
import com.study.modules.notification.Notification;
import com.study.modules.notification.NotificationService;
import com.study.modules.notification.NotificationType;
import com.study.modules.study.Study;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Async
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {
	
	private final NotificationService notificationService; 
	private final JavaMailSender javaMailSender;
	
	@EventListener
	public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
		Account account = enrollmentEvent.getEnrollment().getAccount();
		Event event = enrollmentEvent.getEnrollment().getEvent();
		Study study = event.getStudy();
		String message = enrollmentEvent.getMessage();
		
		if(account.isStudyEnrollmentResultByEmail()) {
			this.sendEmailNotification(account, study, message);
		} 
		
		if(account.isStudyEnrollmentResultByWeb()) {
			this.sendWebNotification(account, study, message, NotificationType.EVENT_ENROLLMENT);
		}
	}
	private void sendWebNotification(Account account, Study study, String message, NotificationType notificationType) {
		Notification notification = Notification.builder()
				.account(account)
				.title("스터디 생성 공지")
				.checked(false)
				.createdAt(LocalDateTime.now())
				.message(message)
				.notificationType(notificationType)
				.link("/study/" + study.getEncodePath()).build();
		notificationService.saveNotification(notification);
	}
	
	private void sendEmailNotification(Account account, Study study, String message) {
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setFrom("admin@studyhard.com");
		simpleMessage.setTo(account.getEmail());
		simpleMessage.setSubject("빡공에서 알려드립니다~~" + study.getTitle() +" 공지");
		simpleMessage.setText(message);
		javaMailSender.send(simpleMessage);
	}

}
