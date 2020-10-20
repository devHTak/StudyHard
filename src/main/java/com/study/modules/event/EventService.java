package com.study.modules.event;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.event.form.EventForm;
import com.study.modules.study.Study;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class EventService {
	
	private final EventRepository eventRepository;
	
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
		
		return eventRepository.save(event);
	}
	
	public List<Event> findWithEnrollmentsByStudyOrderByStartDateTime(Study study) {
		return eventRepository.findWithEnrollmentsByStudyOrderByStartDateTime(study);
	}

	public void updateEvent(Event event, @Valid EventForm eventForm) {
		event.setDescription(eventForm.getDescription());
		event.setStartDateTime(eventForm.getStartDateTime());
		event.setEndDateTime(eventForm.getEndDateTime());
		event.setEndEnrollmentDateTime(eventForm.getEndEnrollmentDateTime());
		event.setEventType(eventForm.getEventType());
		event.setTitle(eventForm.getTitle());
		event.setLimitOfEnrollments(eventForm.getLimitOfEnrollments());
	}

	public void delete(Event event) {
		eventRepository.delete(event);
	}

}
