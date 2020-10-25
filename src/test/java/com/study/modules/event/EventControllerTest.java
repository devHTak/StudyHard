package com.study.modules.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.account.AccountRepository;
import com.study.modules.account.AccountService;
import com.study.modules.account.WithAccount;
import com.study.modules.account.form.SignUpForm;
import com.study.modules.event.form.EventForm;
import com.study.modules.study.Study;
import com.study.modules.study.StudyRepository;
import com.study.modules.study.StudyService;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class EventControllerTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired EventService eventService;
	@Autowired StudyService studyService;
	@Autowired AccountRepository accountRepository;
	@Autowired StudyRepository studyRepository;
	@Autowired EventRepository eventRepository;
	@Autowired EnrollmentRepository enrollmentRepository;
	
	@BeforeEach
	public void beforeEach() {
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(()-> new IllegalArgumentException());
		
		Study study = Study.builder()
				.path("study-test")
				.title("test")
				.shortDescription("test")
				.fullDescription("testtest")
				.published(true)
				.publishedDateTime(LocalDateTime.now()).build();
		
		study.addManager(account);
		
		studyRepository.save(study);
	}
	
	@AfterEach
	public void afterEach() {
		enrollmentRepository.deleteAll();
		eventRepository.deleteAll();
		studyRepository.deleteAll();
		accountRepository.deleteAll();
	}
	
	@DisplayName("이벤트 생성 폼 확인")
	@WithAccount(value = "testNickname")
	@Test
	public void getNewEventFormTest() throws Exception {
		mockMvc.perform(get("/study/{path}/new-event", "study-test"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(model().attributeExists("eventForm"))
				.andExpect(view().name("event/new-event"));
	}
	
	@DisplayName("신규 이벤트 생성 - 성공")
	@WithAccount(value = "testNickname")
	@Test
	public void addNewEventSuccessTest() throws Exception {
		mockMvc.perform(post("/study/{path}/new-event", "study-test")
				.param("title", "test")
				.param("limitOfEnrollments", "2")
				.param("description", "test")
				.param("eventType", "FCFS")
				.param("endEnrollmentDateTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("startDateTime", LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("endDateTime", LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_DATE_TIME))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events"));
		
		Study study = studyService.getOnlyStudyByPath("study-test");
		List<Event> events = eventService.findWithEnrollmentsByStudyOrderByStartDateTime(study);
		
		assertTrue(events.get(0).getTitle().equals("test"));
	}
	
	@DisplayName("신규 이벤트 생성 - 실패")
	@WithAccount(value = "testNickname")
	@Test
	public void addNewEvenFailTest() throws Exception {
		mockMvc.perform(post("/study/{path}/new-event", "study-test")
				.param("title", "test")
				.param("limitOfEnrollments", "2")
				.param("description", "test")
				.param("eventType", "FCFS")
				.param("endEnrollmentDateTime", LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("startDateTime", LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("endDateTime", LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_DATE_TIME))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("study"))
			.andExpect(view().name("event/new-event"));
		
		Study study = studyService.getOnlyStudyByPath("study-test");
		List<Event> events = eventService.findWithEnrollmentsByStudyOrderByStartDateTime(study);
		
		assertTrue(events.size() == 0);
	}
	
	@DisplayName("이벤트 리스트 확인")
	@WithAccount(value = "testNickname")
	@Test
	public void getAllEventsViewTest() throws Exception {
		mockMvc.perform(get("/study/{path}/events", "study-test"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("study"))
			.andExpect(model().attributeExists("newEvents"))
			.andExpect(model().attributeExists("oldEvents"))
			.andExpect(view().name("event/events"));
	} 
	
	@DisplayName("이벤트 조회")
	@WithAccount(value = "testNickname")
	@Test
	public void getEventViewTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		
		mockMvc.perform(get("/study/{path}/events/{eventId}", "study-test", event.getId()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("study"))
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("event"))
			.andExpect(view().name("event/view"));
	}
	
	@DisplayName("이벤트 수정 폼 확인")
	@WithAccount("testNickname")
	@Test
	public void getUpdateEventFormTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		
		mockMvc.perform(get("/study/{path}/events/{eventId}/update", "study-test", event.getId()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("study"))
			.andExpect(model().attributeExists("event"))
			.andExpect(model().attributeExists("eventForm"))
			.andExpect(view().name("event/update-event"));
	}
	
	@DisplayName("이벤트 수정 성공")
	@WithAccount("testNickname")
	@Test
	public void updateEventSuccessTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/update", "study-test", event.getId())
				.param("title", "test2")
				.param("limitOfEnrollments", "2")
				.param("description", "test2")
				.param("eventType", "FCFS")
				.param("endEnrollmentDateTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("startDateTime", LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("endDateTime", LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_DATE_TIME))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		Study study = studyService.getOnlyStudyByPath("study-test");
		List<Event> events = eventService.findWithEnrollmentsByStudyOrderByStartDateTime(study);
		assertTrue(events.get(0).getTitle().equals("test2"));
	}
	
	@DisplayName("이벤트 수정 실패")
	@WithAccount("testNickname")
	@Test
	public void updateEventFailTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/update", "study-test", event.getId())
				.param("title", "test2")
				.param("limitOfEnrollments", "1")
				.param("description", "test2")
				.param("eventType", "FCFS")
				.param("endEnrollmentDateTime", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("startDateTime", LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ISO_DATE_TIME))
				.param("endDateTime", LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ISO_DATE_TIME))
				.with(csrf()))
			.andDo(print())
			.andExpect(model().hasErrors())
			.andExpect(model().attributeExists("account"))
			.andExpect(model().attributeExists("study"))
			.andExpect(model().attributeExists("event"))
			.andExpect(view().name("event/update-event"));					
		
		Study study = studyService.getOnlyStudyByPath("study-test");
		List<Event> events = eventService.findWithEnrollmentsByStudyOrderByStartDateTime(study);
		assertTrue(events.get(0).getTitle().equals("test"));
	}
	
	@DisplayName("이벤트 삭제")
	@WithAccount("testNickname")
	@Test
	public void deleteEventTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		mockMvc.perform(delete("/study/{path}/events/{eventId}", "study-test", event.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events"));
	}
	
	@DisplayName("모임 등록 - 수락")
	@WithAccount("testNickname")
	@Test
	public void acceptedEnrollEventTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(() -> new IllegalArgumentException());
		mockMvc.perform(post("/study/{path}/events/{eventId}/enroll", "study-test", event.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event)
				.orElseThrow(() -> new IllegalArgumentException());
		assertTrue(enrollment.isAccepted());
	}
	
	@DisplayName("모임 등록 - 대기중")
	@WithAccount("testNickname")
	@Test
	public void notAcceptedEnrollEventTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account test1 = this.makeNewAccountEnrolled("test1", "test1@test.com", event);
		Account test2 = this.makeNewAccountEnrolled("test2", "test2@test.com", event);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(() -> new IllegalArgumentException());
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/enroll", "study-test", event.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
	
		Enrollment enrollmentByAccount = enrollmentRepository.findByAccountAndEvent(account, event)
				.orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollmentByTest1 = enrollmentRepository.findByAccountAndEvent(test1, event)
				.orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollmentByTest2 = enrollmentRepository.findByAccountAndEvent(test2, event)
				.orElseThrow(() -> new IllegalArgumentException());
		
		assertFalse(enrollmentByAccount.isAccepted());
		assertTrue(enrollmentByTest1.isAccepted());
		assertTrue(enrollmentByTest2.isAccepted());
	}
	
	@DisplayName("모임 등록 취소 - 자동 참가 신청 수락")
	@WithAccount("testNickname")
	@Test
	public void disenrollEventTestWithAutoEnrolled() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(() -> new IllegalArgumentException());
		eventService.enrollEvent(event, account);
		Account test1 = this.makeNewAccountEnrolled("test1", "test1@test.com", event);
		Account test2 = this.makeNewAccountEnrolled("test2", "test2@test.com", event);
		
		Enrollment enrollByAccount = enrollmentRepository.findByAccountAndEvent(account, event).orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollByTest1 = enrollmentRepository.findByAccountAndEvent(test1, event).orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollByTest2 = enrollmentRepository.findByAccountAndEvent(test2, event).orElseThrow(() -> new IllegalArgumentException());
		
		assertTrue(enrollByAccount.isAccepted());
		assertTrue(enrollByTest1.isAccepted());
		assertFalse(enrollByTest2.isAccepted());
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/disenroll", "study-test", event.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertTrue(enrollByTest1.isAccepted());
		assertTrue(enrollByTest2.isAccepted());
	}
	
	@DisplayName("모임 등록 취소 - 자동 참가 신청 수락 안되는 경우")
	@WithAccount("testNickname")
	@Test
	public void disenrollEventTestWithNotAutoEnroll() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account test1 = this.makeNewAccountEnrolled("test1", "test1@test.com", event);
		Account test2 = this.makeNewAccountEnrolled("test2", "test2@test.com", event);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(() -> new IllegalArgumentException());
		eventService.enrollEvent(event, account);
		
		Enrollment enrollByAccount = enrollmentRepository.findByAccountAndEvent(account, event).orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollByTest1 = enrollmentRepository.findByAccountAndEvent(test1, event).orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollByTest2 = enrollmentRepository.findByAccountAndEvent(test2, event).orElseThrow(() -> new IllegalArgumentException());
		
		assertFalse(enrollByAccount.isAccepted());
		assertTrue(enrollByTest1.isAccepted());
		assertTrue(enrollByTest2.isAccepted());
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/disenroll", "study-test", event.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertFalse(enrollByAccount.isAccepted());
		assertTrue(enrollByTest1.isAccepted());
		assertTrue(enrollByTest2.isAccepted());
	}	
	
	
	@DisplayName("모임 등록 - 수락")
	@WithAccount("testNickname")
	@Test
	public void acceptEnrollmentTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.CONFIRMATIVE);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(()-> new IllegalArgumentException());
		Enrollment enrollment = eventService.enrollEvent(event, account);
		
		assertFalse(enrollment.isAccepted());
		mockMvc.perform(post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/accept",
				"study-test", event.getId(), enrollment.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertTrue(enrollment.isAccepted());
	}
	
	@DisplayName("모임 등록 - 거절, 인원 초과로 인해 수락 미동작")
	@WithAccount("testNickname")
	@Test
	public void notAcceptEnrollmentTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account test1 = this.makeNewAccount("test1", "test1@test.com", event);
		Enrollment enrollByTest1 = eventService.enrollEvent(event, test1);
		eventService.acceptEnrollment(event, enrollByTest1);
		Account test2 = this.makeNewAccount("test2", "test2@test.com", event);
		Enrollment enrollByTest2 = eventService.enrollEvent(event, test2);
		eventService.acceptEnrollment(event, enrollByTest2);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(() -> new IllegalArgumentException());
		Enrollment enrollByAccount = eventService.enrollEvent(event, account);
		
		assertFalse(enrollByAccount.isAccepted());
		assertTrue(enrollByTest1.isAccepted());
		assertTrue(enrollByTest2.isAccepted());
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/disenroll", "study-test", event.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertFalse(enrollByAccount.isAccepted());
		assertTrue(enrollByTest1.isAccepted());
		assertTrue(enrollByTest2.isAccepted());
	}
	
	@DisplayName("모임 등록 - 거절")
	@WithAccount("testNickname")
	@Test
	public void rejectEnrollmentTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.CONFIRMATIVE);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(()-> new IllegalArgumentException());
		Enrollment enrollment = eventService.enrollEvent(event, account);
		
		assertFalse(enrollment.isAccepted());
		mockMvc.perform(post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/reject",
				"study-test", event.getId(), enrollment.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertFalse(enrollment.isAccepted());
	}
	
	@DisplayName("모임 참석")
	@WithAccount("testNickname")
	@Test
	public void checkInEnrollmentTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(()-> new IllegalArgumentException());
		Enrollment enrollment = eventService.enrollEvent(event, account);
		
		assertFalse(enrollment.isAttended());
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/check-in",
				"study-test", event.getId(), enrollment.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertTrue(enrollment.isAttended());
	}
	
	@DisplayName("모임 참석")
	@WithAccount("testNickname")
	@Test
	public void cancelCheckInEnrollmentTest() throws Exception {
		Event event = this.makeNewEvent("test", EventType.FCFS);
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(()-> new IllegalArgumentException());
		Enrollment enrollment = eventService.enrollEvent(event, account);
		eventService.checkInEnrollment(event, enrollment);
		
		assertTrue(enrollment.isAttended());
		
		mockMvc.perform(post("/study/{path}/events/{eventId}/enrollments/{enrollmentId}/cancel-check-in",
				"study-test", event.getId(), enrollment.getId())
				.with(csrf()))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/study/study-test/events/" + event.getId()));
		
		assertFalse(enrollment.isAttended());
	}
	
	private Event makeNewEvent(String title, EventType eventType) {
		Account account = accountRepository.findByNickname("testNickname")
				.orElseThrow(() -> new IllegalArgumentException());
		Study study = studyService.getOnlyStudyByPath("study-test");
		EventForm eventForm = EventForm.builder()
				.title(title)
				.description("test")
				.limitOfEnrollments(2)
				.eventType(eventType)
				.endEnrollmentDateTime(LocalDateTime.now().plusDays(1))
				.startDateTime(LocalDateTime.now().plusDays(2))
				.endDateTime(LocalDateTime.now().plusDays(3)).build();
		
		return eventService.addNewEvent(account, study, eventForm);
	}
	
	private Account makeNewAccountEnrolled(String nickname, String email, Event event) {
		SignUpForm signUpForm = SignUpForm.builder()
				.email(email)
				.nickname(nickname)
				.password("12341234").build();
				
		Account account = accountService.signUp(signUpForm);
		
		eventService.enrollEvent(event, account);
		return account;
	}
	
	private Account makeNewAccount(String nickname, String email, Event event) {
		SignUpForm signUpForm = SignUpForm.builder()
				.email(email)
				.nickname(nickname)
				.password("12341234").build();
				
		Account account = accountService.signUp(signUpForm);
		
		return account;
	}
	
}