package com.study.modules.account;

import java.util.List;
import java.util.Set;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.modules.account.form.NicknameForm;
import com.study.modules.account.form.NotificationForm;
import com.study.modules.account.form.PasswordForm;
import com.study.modules.account.form.ProfileForm;
import com.study.modules.account.validator.PasswordFormValidator;
import com.study.modules.tag.Tag;
import com.study.modules.tag.TagForm;
import com.study.modules.tag.TagService;
import com.study.modules.zone.Zone;
import com.study.modules.zone.ZoneForm;
import com.study.modules.zone.ZoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {
	
	private final AccountService accountService;
	private final SettingsService settingsService;
	private final TagService tagService;
	private final ZoneService zoneService;
	private final PasswordFormValidator passwordFormValidator;
	private final ObjectMapper objectMapper;
	
	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(passwordFormValidator);
	}
	
	@GetMapping("/profile")
	public String getProfileForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new ProfileForm(account));
		
		return "settings/profile";
	}
	
	@PostMapping("/profile")
	public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute ProfileForm profileForm, Errors errors, Model model, RedirectAttributes attribute) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "settings/profile";
		}
		
		settingsService.updateProfile(account, profileForm);
		attribute.addFlashAttribute("updateMsg", "프로필 업데이트에 성공하였습니다.");
		return "redirect:/settings/profile";
	}
	
	@GetMapping("/password")
	public String getPasswordForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new PasswordForm());
		
		return "settings/password";
	}
	
	@PostMapping("/password")
	public String updatePassword(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm, Errors errors, Model model, RedirectAttributes attribute) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "settings/password";
		}
		
		settingsService.updatePassword(account, passwordForm);
		attribute.addFlashAttribute("updateMsg", "패스워드 변경에 성공하였습니다.");
		return "redirect:/settings/password";
	}
	
	@GetMapping("/notification")
	public String getNotificationForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new NotificationForm(account));
		
		return "settings/notification";
	}
	
	@PostMapping("/notification")
	public String updateNotification(@CurrentUser Account account, @Valid @ModelAttribute NotificationForm notificationForm, Errors errors, Model model, RedirectAttributes attribute) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "settings/notification";
		}
		
		settingsService.updateNotification(account, notificationForm);
		attribute.addFlashAttribute("updateMsg", "알림 변경에 성공하였습니다.");
		return "redirect:/settings/notification";
	}
	
	@GetMapping("/account")
	public String getNicknameForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new NicknameForm(account));
		
		return "settings/account";
	}
	
	@PostMapping("/account")
	public String updateNickname(@CurrentUser Account account, @Valid @ModelAttribute NicknameForm nicknameForm, Errors errors, Model model, RedirectAttributes attribute) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "settings/account";
		}
		
		settingsService.updateNickname(account, nicknameForm);
		attribute.addFlashAttribute("updateMsg", "닉네임 변경에 성공하였습니다.");
		return "redirect:/settings/account";
	}
	
	@GetMapping("/tags")
	public String getTagsForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
		Set<Tag> tags = accountService.findAccountWithTagsById(account).getTags();
		List<String> whitelist = tagService.findAll().stream().map(Tag :: getTitle).collect(Collectors.toList());
		
		model.addAttribute(account);
		model.addAttribute("tags", tags.stream().map(Tag :: getTitle).collect(Collectors.toList()));
		model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
		model.addAttribute(new TagForm());
		
		return "settings/tags";
	}
	
	@PostMapping("/tags/add")
	public ResponseEntity addTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
		Tag tag = tagService.save(tagForm);
		accountService.addTag(account, tag);
		return ResponseEntity.ok().build();		
	}
	
	@PostMapping("/tags/remove")
	public ResponseEntity removeTags(@CurrentUser Account account, @RequestBody TagForm tagForm) {
		Tag tag = tagService.remove(tagForm);
		if(tag.getTitle() == null || !tag.getTitle().equals(tagForm.getTitle())) {
			return ResponseEntity.badRequest().build();
		}
		accountService.removeTag(account, tag);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/zones")
	public String getZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
		Set<Zone> zones = accountService.findAccountWithZonesById(account).getZones();
		List<String> whitelist = zoneService.findAll().stream().map(Zone :: toString).collect(Collectors.toList());
		
		model.addAttribute(account);
		model.addAttribute("zones", zones.stream().map(Zone :: toString).collect(Collectors.toList()));
		model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
		
		return "settings/zones";
	}
	
	@PostMapping("/zones/add")
	public ResponseEntity addZones(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneService.findByCity(zoneForm);
		if(zone.getCity() == null || !zone.toString().equals(zoneForm.getFullCity())) {
			return ResponseEntity.badRequest().build();
		}
		accountService.addZone(account, zone);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/zones/remove")
	public ResponseEntity removeZones(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		Zone zone = zoneService.findByCity(zoneForm);
		if(zone.getCity() == null || !zone.toString().equals(zoneForm.getFullCity())) {
			return ResponseEntity.badRequest().build();
		}
		accountService.removeZone(account, zone);
		return ResponseEntity.ok().build();
	}
}
