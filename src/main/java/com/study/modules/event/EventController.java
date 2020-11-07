package com.study.modules.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.modules.account.Account;
import com.study.modules.account.CurrentUser;
import com.study.modules.event.form.EventForm;
import com.study.modules.event.validator.EventFormValidator;
import com.study.modules.study.Study;
import com.study.modules.study.StudyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {
	
	private final EventFormValidator eventFormValidator;
	private final StudyService studyService;
	private final EventService eventService;
	
	@InitBinder("eventForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(eventFormValidator);
	}
	
	@GetMapping("/new-event")
	public String getNewEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		model.addAttribute("account", account);
		model.addAttribute("study", studyService.getOnlyStudyByPath(path));
		model.addAttribute(new EventForm());
		
		return "event/new-event";
	}
	
	@PostMapping("/new-event")
	public String addNewEvent(@CurrentUser Account account, @PathVariable String path, @Valid @ModelAttribute EventForm eventForm, Errors errors, Model model) {
		Study study = studyService.getOnlyStudyByPath(path);
		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			return "event/new-event";
		}
		eventService.addNewEvent(account, study, eventForm);
		return "redirect:/study/" + study.getEncodePath() + "/events";
	}
	
	@GetMapping("/events")
	public String getAllEventsView(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyByPath(path);
		List<Event> events = eventService.findWithEnrollmentsByStudyOrderByStartDateTime(study);
		List<Event> oldEvents = new ArrayList<>();
		List<Event> newEvents = new ArrayList<>();
		events.forEach(event -> {
			if(event.getEndDateTime().isBefore(LocalDateTime.now())) {
				oldEvents.add(event);
			} else {
				newEvents.add(event);
			}
		}); 
		
		model.addAttribute("account", account);
		model.addAttribute("study", study);
		model.addAttribute("newEvents", newEvents);
		model.addAttribute("oldEvents", oldEvents);
		return "event/events";
	}
	
	@GetMapping("/events/{eventId}")
	public String getEventView(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, Model model) {
		
		Study study = studyService.getStudyWithMembersAndManagersByPath(path);
		model.addAttribute("account", account);
		model.addAttribute("study", study);
		model.addAttribute("event", event);
		
		return "event/view";
	}
	
	@GetMapping("/events/{eventId}/update")
	public String getUpdateEventForm(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, Model model) {
		model.addAttribute("account", account);
		model.addAttribute("study", studyService.getOnlyStudyByPath(path));
		model.addAttribute("event", event);
		model.addAttribute(new EventForm());
		
		return "event/update-event";
	}
	
	@PostMapping("/events/{eventId}/update")
	public String updateEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @Valid @ModelAttribute EventForm eventForm, Errors errors, Model model) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventFormValidator.updateEventValidator(event, eventForm, errors);
		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			model.addAttribute(event);
			return "event/update-event";
		}
		
		eventService.updateEvent(study, event, eventForm);
		return "redirect:/study/" + study.getEncodePath() +"/events/" + event.getId();
	}
	
	@DeleteMapping("/events/{eventId}")
	public String deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.delete(study, event);
		
		return "redirect:/study/" + study.getEncodePath() +"/events";
	}
	
	@PostMapping("/events/{eventId}/enroll")
	public String enrollEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.enrollEvent(event, account);
		
		return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
	}
	
	@PostMapping("/events/{eventId}/disenroll")
	public String disenrollEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.disenrollEvent(event, account);
		
		return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
	}
	
	@PostMapping("/events/{eventId}/enrollments/{enrollmentId}/accept") 
	public String acceptEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.acceptEnrollment(event, enrollment);
		
		return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
	}
	
	@PostMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
	public String rejectEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.rejectEnrollment(event, enrollment);
		
		return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
	}
	
	@PostMapping("/events/{eventId}/enrollments/{enrollmentId}/check-in")
	public String checkInEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.checkInEnrollment(event, enrollment);
		
		return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
	}
	
	@PostMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-check-in")
	public String cancelCheckInEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable("eventId") Event event, @PathVariable("enrollmentId") Enrollment enrollment) {
		Study study = studyService.getOnlyStudyByPath(path);
		eventService.cancelCheckInEnrollment(event, enrollment);
		
		return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
	}

}
