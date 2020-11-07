package com.study.modules.event;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.event.event.EnrollmentAcceptEvent;
import com.study.modules.event.event.EnrollmentRejectEvent;
import com.study.modules.event.form.EventForm;
import com.study.modules.study.Study;
import com.study.modules.study.event.StudyUpdatedEvent;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class EventService {
	
	private final EventRepository eventRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final ApplicationEventPublisher eventPublisher;
	
	public Event addNewEvent(Account account, Study study, EventForm eventForm) {
		Event event = Event.builder()
				.createDateTime(LocalDateTime.now())
				.study(study)
				.createdBy(account)
				.description(eventForm.getDescription())
				.startDateTime(eventForm.getStartDateTime())
				.endDateTime(eventForm.getEndDateTime())
				.endEnrollmentDateTime(eventForm.getEndEnrollmentDateTime())
				.eventType(eventForm.getEventType())
				.title(eventForm.getTitle())
				.limitOfEnrollments(eventForm.getLimitOfEnrollments()).build();
		
		// 모임 생성 이벤트 발생
		eventPublisher.publishEvent(new StudyUpdatedEvent(study, study.getTitle() + " 스터디에 새로운 모임인 " + event.getTitle() +"이 생성되었습니다."));
		
		return eventRepository.save(event);
	}
	
	public List<Event> findWithEnrollmentsByStudyOrderByStartDateTime(Study study) {
		return eventRepository.findWithEnrollmentsByStudyOrderByStartDateTime(study);
	}

	public void updateEvent(Study study, Event event, @Valid EventForm eventForm) {
		event.setDescription(eventForm.getDescription());
		event.setStartDateTime(eventForm.getStartDateTime());
		event.setEndDateTime(eventForm.getEndDateTime());
		event.setEndEnrollmentDateTime(eventForm.getEndEnrollmentDateTime());
		event.setEventType(eventForm.getEventType());
		event.setTitle(eventForm.getTitle());
		event.setLimitOfEnrollments(eventForm.getLimitOfEnrollments());
		
		// 모임 변경 이벤트 발생
		eventPublisher.publishEvent(new StudyUpdatedEvent(study, study.getTitle() + " 스터디에 " + event.getTitle() +" 모임이 변경되었습니다."));
	}

	public void delete(Study study, Event event) {
		eventRepository.delete(event);
		
		// 모임 생성 이벤트 발생
		eventPublisher.publishEvent(new StudyUpdatedEvent(study, study.getTitle() + " 스터디에 " + event.getTitle() +" 모임이 삭제되었습니다.."));
	}

	public Enrollment enrollEvent(Event event, Account account) {
		boolean accepted = false;
		if( event.getEventType().equals(EventType.FCFS) && 
				event.getEnrollments().stream().filter(Enrollment::isAccepted).count() < event.getLimitOfEnrollments()) {
			accepted = true;
		} 
		
		Enrollment enrollment = Enrollment.builder()
				.account(account)
				.enrolledAt(LocalDateTime.now())
				.event(event)
				.accepted(accepted)
				.build();
		
		event.getEnrollments().add(enrollment);
		
		return enrollmentRepository.save(enrollment);		
	}
	
	public void disenrollEvent(Event event, Account account) {
		Enrollment enrollment = enrollmentRepository.findByAccountAndEvent(account, event)
				.orElseThrow(()-> new IllegalArgumentException());
		
		if(!enrollment.isAttended()) {
			event.getEnrollments().remove(enrollment);
			
			if(event.getEventType().equals(EventType.FCFS)) {
				event.getEnrollments().forEach( e -> {
					if(!e.isAttended()) {
						e.setAccepted(true);
						return;
					}
				});
			}
			
			enrollmentRepository.delete(enrollment);
		}
	}
	
	public void acceptEnrollment(Event event, Enrollment enrollment) {
		if(event.getEventType().equals(EventType.CONFIRMATIVE) && 
				event.getEnrollments().stream().filter(Enrollment::isAttended).count() < event.getLimitOfEnrollments()) {			
			enrollment.setAccepted(true);
			
			// 모임 생성 이벤트 발생
			eventPublisher.publishEvent(new EnrollmentAcceptEvent(enrollment));
		}
	}
	
	public void rejectEnrollment(Event event, Enrollment enrollment) {
		if(event.getEventType().equals(EventType.CONFIRMATIVE)) {
			enrollment.setAccepted(false);
			// 모임 생성 이벤트 발생
			eventPublisher.publishEvent(new EnrollmentRejectEvent(enrollment));
		}
	}
	
	public void cancelCheckInEnrollment(Event event, Enrollment enrollment) {
		enrollment.setAttended(false);
	}
	
	public void checkInEnrollment(Event event, Enrollment enrollment) {
		enrollment.setAttended(true);
	}
}
