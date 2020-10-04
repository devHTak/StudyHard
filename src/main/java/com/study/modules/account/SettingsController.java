package com.study.modules.account;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.study.modules.account.form.NotificationForm;
import com.study.modules.account.form.PasswordForm;
import com.study.modules.account.form.ProfileForm;
import com.study.modules.account.validator.PasswordFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

	private final SettingsService settingsService;
	private final PasswordFormValidator passwordFormValidator;
	
	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(passwordFormValidator);
	}
	
	@GetMapping("/profile")
	public String getProfileForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new ProfileForm());
		
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
		model.addAttribute(new NotificationForm());
		
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
}
