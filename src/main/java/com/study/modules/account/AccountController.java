package com.study.modules.account;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.study.modules.account.form.SignUpForm;
import com.study.modules.account.validator.SignUpFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final AccountService accountService;
	private final SignUpFormValidator signUpFormValidator;
	
	@InitBinder("signUpForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(signUpFormValidator);
	}
	
	@GetMapping("/sign-up")
	public String signUpForm(Model model) {
		model.addAttribute(new SignUpForm());
		
		return "account/sign-up";
	}
	
	@PostMapping("/sign-up")
	public String signUp(@Valid @ModelAttribute SignUpForm signUpForm, Errors error, Model model, RedirectAttributes attribute) {
		if(error.hasErrors()) {
			return "account/sign-up";
		}
		
		Account account = accountService.signUp(signUpForm);
		accountService.login(account);
		return "redirect:/";
	}
	
	@GetMapping("/check-email-token")
	public String checkEmailToken(@RequestParam String token, @RequestParam String nickname, Model model) {
		boolean isCheck = accountService.checkEmailToken(token, nickname);
		int count = accountService.count();
		
		model.addAttribute("isCheck", isCheck);
		model.addAttribute("count", count);
		model.addAttribute("nickname", nickname);
		
		return "account/confirm-email";
	}
	
	@GetMapping("/resend-email/{nickname}")
	public String resendEmail(@PathVariable String nickname, RedirectAttributes attributes) {
		Account account = accountService.findByNickname(nickname);
		
		if(account.canSendEmail()) {
			accountService.sendEmail(account);
			attributes.addFlashAttribute("emailMessage", "이메일을 다시 전송했습니다.");
		} else {
			attributes.addFlashAttribute("emailMessage", "이메일 전송에  실패했습니다.");
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/profile/{nickname}")
	public String profile(@CurrentUser Account account, @PathVariable String nickname, Model model) {
		Account byNickname = accountService.findByNickname(nickname);
		
		model.addAttribute("account", byNickname);
		return "account/profile";
	}
	
}
