package com.study.modules.study;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.account.AccountRepository;
import com.study.modules.account.AccountService;
import com.study.modules.account.form.SignUpForm;
import com.study.modules.notification.Notification;
import com.study.modules.notification.NotificationRepository;
import com.study.modules.notification.NotificationService;
import com.study.modules.notification.NotificationType;
import com.study.modules.study.event.StudyCreatedEvent;
import com.study.modules.study.form.StudyForm;
import com.study.modules.tag.Tag;
import com.study.modules.tag.TagForm;
import com.study.modules.tag.TagRepository;
import com.study.modules.tag.TagService;
import com.study.modules.zone.Zone;
import com.study.modules.zone.ZoneService;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class StudyNotificationTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired ApplicationEventPublisher applicationEventPulbisher;
	@Autowired NotificationService notificationService;
	@Autowired NotificationRepository notificationRepository;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	@Autowired StudyService studyService;
	@Autowired StudyRepository studyRepository;
	@Autowired ZoneService zoneService;
	@Autowired TagService tagService;
	@Autowired TagRepository tagRepository;
	@Autowired ThreadPoolTaskExecutor executor;
	
	@BeforeEach
	public void boforeEach() {
		SignUpForm signUpForm = SignUpForm.builder()
				.email("test@test.com")
				.nickname("test")
				.password("12341234")
				.build();
		Account account = accountService.signUp(signUpForm);
		account.setStudyCreatedByEmail(true);
		account.setStudyCreatedByWeb(true);
		account.setStudyUpdatedByEmail(true);
		account.setStudyUpdatedByWeb(true);
		
		TagForm tagForm = TagForm.builder().title("test").build();
		Zone zone = zoneService.findAll().get(0);
		Tag tag = tagService.save(tagForm);
		accountService.addTag(account, tag);
		accountService.addZone(account, zone);
		
		StudyForm studyForm = StudyForm.builder()
				.fullDescription("test")
				.path("test")
				.shortDescription("test")
				.title("test").build();
		Study study = studyService.save(studyForm, account);
		studyService.addTag(study, tag);
		studyService.addZone(study, zone);	
	}
	
	@AfterEach
	public void afterEach() {
		accountRepository.deleteAll();
		studyRepository.deleteAll();
		tagRepository.deleteAll();
		notificationRepository.deleteAll();
	}
	
	@DisplayName("Study Created Event")
	@Test
	public void studyCreateEventTest() throws Exception {
		Study study = studyService.getOnlyStudyByPath("test");
		Account account = accountService.findByNickname("test");
		applicationEventPulbisher.publishEvent(new StudyCreatedEvent(study));
		
		executor.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
		
		List<Notification> notifications = notificationService.findByNotificationType(NotificationType.STUDY_CREATED);
		assertTrue(notifications.stream().map(Notification::getAccount).collect(Collectors.toList()).contains(account));
	}

}
