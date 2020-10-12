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
import com.study.modules.study.form.PathForm;
import com.study.modules.study.form.TitleForm;
import com.study.modules.study.validator.PathFormValidator;
import com.study.modules.tag.Tag;
import com.study.modules.tag.TagForm;
import com.study.modules.tag.TagService;
import com.study.modules.zone.Zone;
import com.study.modules.zone.ZoneForm;
import com.study.modules.zone.ZoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}/settings")
public class StudySettingsController {
	
	private final StudyService studyService;
	private final ZoneService zoneService;
	private final TagService tagService;
	private final ObjectMapper objectMapper;
	private final PathFormValidator pathFormValidator;
	
	@InitBinder("pathForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(pathFormValidator);
	}

	@GetMapping("/description")
	public String getStudyDescriptionForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute(new DescriptionForm(study));
		
		return "study/settings/description";
	}
	
	@PostMapping("/description")
	public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path, @Valid @ModelAttribute DescriptionForm descriptionForm, Errors errors, RedirectAttributes attribute, Model model) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);

		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			
			return "study/settings/description";
		}
		
		studyService.updateDescription(study, descriptionForm);
		attribute.addFlashAttribute("updateMessage", "업데이트 성공하였습니다.");
		return "redirect:/study/" + study.getEncodePath() +"/settings/description";
	}
	
	@GetMapping("/banner")
	public String getStudyBanner(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		
		model.addAttribute(account);
		model.addAttribute(study);
		
		return "study/settings/banner";
	}
	
	@PostMapping("/useBanner")
	public String useStudyBanner(@CurrentUser Account account, @PathVariable String path, @RequestParam boolean useBanner, RedirectAttributes attribute) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);
		studyService.updateUseBanner(study, useBanner);
		
		attribute.addFlashAttribute("updateMessage", "배너 이미지 사용 여부를 변경하였습니다.");
		return "redirect:/study/" + study.getEncodePath() +"/settings/banner";
	}
	
	@PostMapping("/banner")
	public String updateStudyBanner(@CurrentUser Account account, @PathVariable String path, @RequestParam String bannerImage, RedirectAttributes attribute) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);
		studyService.updateBanner(study, bannerImage);
		
		attribute.addFlashAttribute("updateMessage", "배너 이미지를 변경하였습니다.");
		return "redirect:/study/" + study.getEncodePath() +"/settings/banner";
	}
	
	@GetMapping("/tags")
	public String getStudyTagsForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
		Study study = studyService.getStudyToUpdate(path, account);
		List<String> whitelist = tagService.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
		model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
		
		return "study/settings/tags";
	}
	
	@PostMapping("/tags/add")
	public ResponseEntity addStudyTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
		Study study = studyService.getStudyWithTagsToUpdate(path, account);
		Tag tag = tagService.save(tagForm);
		studyService.addTag(study, tag);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/tags/remove")
	public ResponseEntity removeStudyTag(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm) {
		Study study = studyService.getStudyWithTagsToUpdate(path, account);
		Tag tag = tagService.remove(tagForm);
		studyService.removeTag(study, tag);
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/zones")
	public String getStudyZonesForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
		Study study = studyService.getStudyToUpdate(path, account);
		List<String> whitelist = zoneService.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
		model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
		
		return "study/settings/zones";
	}
	
	@PostMapping("/zones/add")
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
	
	@PostMapping("/zones/remove")
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
	
	@GetMapping("/study")
	public String getStudySettingsForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute(new PathForm());
		model.addAttribute(new TitleForm());
		
		return "study/settings/study";
	}
	
	@PostMapping("/study/open")
	public String openStudy(@CurrentUser Account account, @PathVariable String path) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);
		
		studyService.openStudy(study);
		return "redirect:/study/" + study.getEncodePath() +"/settings/study";
	}
	
	@PostMapping("/study/close")
	public String closeStudy(@CurrentUser Account account, @PathVariable String path) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);
		
		studyService.closeStudy(study);
		return "redirect:/study/" + study.getEncodePath() + "/settings/study";
	}
	
	@PostMapping("/study/recruite/start")
	public String recruiteStartStudy(@CurrentUser Account account, @PathVariable String path) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);
		
		studyService.recruiteStartStudy(study);
		return "redirect:/study/" + study.getEncodePath() + "/settings/study";
	}
	
	@PostMapping("/study/recruite/stop")
	public String recruiteStopStudy(@CurrentUser Account account, @PathVariable String path) {
		Study study = studyService.getStudyWithManagerToUpdate(path, account);
		
		studyService.recruiteStopStudy(study);
		return "redirect:/study/" + study.getEncodePath() +"/settings/study";
	}
	
	@PostMapping("/study/path")
	public String updateStudyPath(@CurrentUser Account account, @PathVariable String path, @Valid @ModelAttribute PathForm pathForm, Errors error, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		if(error.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			model.addAttribute(new TitleForm());
			return "study/settings/study"; 
		}
		
		studyService.updateStudyPath(study, pathForm);
		return "redirect:/study/" + study.getEncodePath() +"/settings/study";
	}
	
	@PostMapping("/study/title")
	public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path, @Valid @ModelAttribute TitleForm titleForm, Errors error, Model model) {
		Study study = studyService.getStudyToUpdate(path, account);
		if(error.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			model.addAttribute(new PathForm());
			return "study/settings/study";
		}
		
		studyService.updateStudyTitle(study, titleForm);
		return "redirect:/study/" + study.getEncodePath() + "/settings/study";
	}
	
	@PostMapping("/study/remove")
	public String removeStudy(@CurrentUser Account account, @PathVariable String path) {
		studyService.deleteByPath(path);
		
		return "redirect:/";
	}
}
