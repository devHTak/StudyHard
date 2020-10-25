package com.study.modules.event.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.event.Event;
import com.study.modules.event.form.EventForm;

@Component
public class EventFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return EventForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		EventForm eventForm = (EventForm)target;
		
		if(this.isBeforeEndEnrollmentDateTimeToNow(eventForm)) {
			errors.rejectValue("endEnrollmentDateTime", "wrong.datetime", "등록 마감 일시를 정확히 해주세요.");
		}
		
		if(this.isBeforeStartDateTimeToEndEnrollmentDateTime(eventForm)) {
			errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 정확히 해주세요.");
		}
		
		if(this.isBeforeEndDateTimeToStartDateTime(eventForm)) {
			errors.rejectValue("endDateTime", "wrong.datetime", "모임 종료 일시를 정확히 해주세요.");
		}
	}
	
	private boolean isBeforeEndEnrollmentDateTimeToNow(EventForm eventForm) {
		return eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
	}
	
	private boolean isBeforeStartDateTimeToEndEnrollmentDateTime(EventForm eventForm) {
		return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
	}
	
	private boolean isBeforeEndDateTimeToStartDateTime(EventForm eventForm) {
		return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime());
	}
	
	public void updateEventValidator(Event event, EventForm eventForm, Errors errors) {
		if(eventForm.getLimitOfEnrollments() == null || event.getLimitOfEnrollments() > eventForm.getLimitOfEnrollments()) {
			errors.rejectValue("limitOfEnrollments", "wrong.limitOfEnrollments", "수정 전 모집인원보다 적을 수는 없습니다.");
		}
		
		if(!event.getEventType().equals(eventForm.getEventType())) {
			errors.rejectValue("eventType", "wrong.eventType", "이벤트 타입을 변경할 수 없습니다.");
		}
	}

}
