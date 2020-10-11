package com.study.modules.study;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.modules.account.Account;
import com.study.modules.account.CurrentUser;
import com.study.modules.study.form.DescriptionForm;
import com.study.modules.study.form.StudyForm;
import com.study.modules.study.form.StudyFormValidator;
import com.study.modules.tag.Tag;
import com.study.modules.tag.TagForm;
import com.study.modules.tag.TagService;
import com.study.modules.zone.Zone;
import com.study.modules.zone.ZoneForm;
import com.study.modules.zone.ZoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class StudyController {
	
	private final StudyService studyService;
	private final ZoneService zoneService;
	private final TagService tagService;
	private final StudyFormValidator studyFormValidator;
	private final ObjectMapper objectMapper;
	
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
	
	@GetMapping("/{path}/settings/description")
	public String getStudyDescriptionForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute(new DescriptionForm(study));
		
		return "study/settings/description";
	}
	
	@PostMapping("/{path}/settings/description")
	public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path, @Valid @ModelAttribute DescriptionForm descriptionForm, Errors errors, RedirectAttributes attribute, Model model) {
		Study study = studyService.getOnlyStudyToUpdate(path, account);

		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			
			return "study/settings/description";
		}
		
		studyService.updateDescription(study, descriptionForm);
		attribute.addFlashAttribute("updateMessage", "업데이트 성공하였습니다.");
		return "redirect:/study/" + study.getEncodePath() +"/settings/description";
	}
	
	@GetMapping("/{path}/settings/banner")
	public String getStudyBanner(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		
		model.addAttribute(account);
		model.addAttribute(study);
		
		return "study/settings/banner";
	}
	
	@PostMapping("/{path}/settings/useBanner")
	public String useStudyBanner(@CurrentUser Account account, @PathVariable String path, @RequestParam boolean useBanner, RedirectAttributes attribute) {
		Study study = studyService.getOnlyStudyToUpdate(path, account);
		studyService.updateUseBanner(study, useBanner);
		
		attribute.addFlashAttribute("updateMessage", "배너 이미지 사용 여부를 변경하였습니다.");
		return "redirect:/study/" + study.getEncodePath() +"/settings/banner";
	}
	
	@PostMapping("/{path}/settings/banner")
	public String updateStudyBanner(@CurrentUser Account account, @PathVariable String path, @RequestParam String bannerImage, RedirectAttributes attribute) {
		Study study = studyService.getOnlyStudyToUpdate(path, account);
		studyService.updateBanner(study, bannerImage);
		
		attribute.addFlashAttribute("updateMessage", "배너 이미지를 변경하였습니다.");
		return "redirect:/study/" + study.getEncodePath() +"/settings/banner";
	}
	
	@GetMapping("/{path}/settings/tags")
	public String getStudyTagsForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
		Study study = studyService.getStudyToUpdate(path, account);
		List<String> whitelist = tagService.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
		model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
		
		return "study/settings/tags";
	}
	
	@PostMapping("/{path}/settings/tags/add")
	public ResponseEntity addStudyTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
		Study study = studyService.getStudyWithTagsToUpdate(path, account);
		Tag tag = tagService.save(tagForm);
		studyService.addTag(study, tag);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{path}/settings/tags/remove")
	public ResponseEntity removeStudyTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
		Study study = studyService.getStudyWithTagsToUpdate(path, account);
		Tag tag = tagService.remove(tagForm);
		studyService.removeTag(study, tag);
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{path}/settings/zones")
	public String getStudyZonesForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
		Study study = studyService.getStudyToUpdate(path, account);
		List<String> whitelist = zoneService.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
		model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
		
		return "study/settings/zones";
	}
	
	@PostMapping("/{path}/settings/zones/add")
	public ResponseEntity addStudyZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
		Study study = studyService.getStudyWithZonesToUpdate(path, account);
		Zone zone = zoneService.findByCity(zoneForm);
		if(zone.getCity() == null || zone.getCity().equals("")) {
			System.out.println("HELLO");
			return ResponseEntity.badRequest().build();
		}
		studyService.addZone(study, zone);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/{path}/settings/zones/remove")
	public ResponseEntity removeStudyZone(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm) {
		Study study = studyService.getStudyWithZonesToUpdate(path, account);
		Zone zone= zoneService.findByCity(zoneForm);
		if(zone.getCity() == null || zone.getCity().equals("")) {
			System.out.println("HELLOA");
			return ResponseEntity.badRequest().build();
		}
			
		studyService.removeZone(study, zone);
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{path}/settings/study")
	public String getStudySettingsForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		
		model.addAttribute(account);
		model.addAttribute(study);
		
		return "study/settings/study";
	}
}
