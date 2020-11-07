package com.study.modules.study.event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.account.AccountPredicate;
import com.study.modules.account.AccountRepository;
import com.study.modules.account.AccountService;
import com.study.modules.notification.Notification;
import com.study.modules.notification.NotificationService;
import com.study.modules.notification.NotificationType;
import com.study.modules.study.Study;
import com.study.modules.study.StudyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Async
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StudyUpdatedEventListener {
	
	private final AccountService accountService;
	private final AccountRepository accountRepository;
	private final StudyService studyService;
	private final NotificationService notificationService;
	private final JavaMailSender javaMailSender;
	
	@EventListener
	public void handleStudyUpdatedEvent(StudyUpdatedEvent event) {
		log.info("StudyCreatedEvent start");
		
		//Study study = event.getStudy();
		Study study = studyService.getStudyWithZonesAndTagsById(event.getStudy().getId());
		Set<Account> accounts = new HashSet<>();
		
		String message = event.getMessage();
		
		NotificationType notificationType;
		notificationType = NotificationType.STUDY_UPDATED;
		Set<Account> managers = study.getManagers();
		Set<Account> members = study.getMembers();
		accounts.addAll(managers);
		accounts.addAll(members);
		
		accounts.forEach(account -> {
			if(account.isStudyCreatedByEmail()) {
				this.sendEmailNotification(account, study, message);
			}
			
			if(account.isStudyCreatedByWeb()) {
				this.sendWebNotification(account, study, message, notificationType);
			}
		});
		
		log.info("StudyCreatedEvent end");
	}
		
	private void sendWebNotification(Account account, Study study, String message, NotificationType notificationType) {
		Notification notification = Notification.builder()
				.account(account)
				.title(message)
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
