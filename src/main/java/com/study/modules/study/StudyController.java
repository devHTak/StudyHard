package com.study.modules.study;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.modules.account.Account;
import com.study.modules.account.CurrentUser;
import com.study.modules.study.form.StudyForm;
import com.study.modules.study.validator.StudyFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {
	
	private final StudyService studyService;
	private final StudyFormValidator studyFormValidator;
	
	@InitBinder("studyForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(studyFormValidator);
	}
	
	@GetMapping("/new-study")
	public String getNewStudyForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new StudyForm());
		
		return "study/form";
	}
	
	@PostMapping("/new-study")
	public String addNewStudy(@CurrentUser Account account, @Valid @ModelAttribute StudyForm studyForm, Errors errors, Model model) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "study/form";
		}
		
		Study study = studyService.save(studyForm, account);
		return "redirect:/study/" + study.getEncodePath();
	}
	
	@GetMapping("/{path}")
	public String getStudyInfo(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyByPath(path);
		
		model.addAttribute(account);
		model.addAttribute(study);
		
		return "study/view";
	}
	
	@GetMapping("/{path}/members")
	public String getStudyMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyByPath(path);
		
		model.addAttribute(account);
		model.addAttribute(study);
		
		return "study/members";
	}
	
	@PostMapping("/{path}/join")
	public String joinStudyMembers(@CurrentUser Account account, @PathVariable String path, Model modle) {
		Study study = studyService.getStudyWithMembersAndManagersByPath(path);
		studyService.joinStudyMembers(study, account);
		
		return "redirect:/study/" + study.getEncodePath();
	}
	
	@PostMapping("/{path}/leave")
	public String leaveStudyMembers(@CurrentUser Account account, @PathVariable String path, Model modle) {
		Study study = studyService.getStudyWithMembersAndManagersByPath(path);
		studyService.removeStudyMembers(study, account);
		
		return "redirect:/study/" + study.getEncodePath();
	}
	
	
}
