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

import com.study.modules.account.form.EmailForm;
import com.study.modules.account.form.SignUpForm;
import com.study.modules.account.validator.EmailFormValidator;
import com.study.modules.account.validator.SignUpFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccountController {
	
	private final AccountService accountService;
	private final SignUpFormValidator signUpFormValidator;
	private final EmailFormValidator emailFormValidator;
	
	@InitBinder("signUpForm")
	public void initBinderSignUpForm(WebDataBinder binder) {
		binder.addValidators(signUpFormValidator);
	}
	
	@InitBinder("emailForm")
	public void initBinderEmailForm(WebDataBinder binder) {
		binder.addValidators(emailFormValidator);
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
	public String checkEmailToken(@RequestParam String token, @RequestParam String email, Model model) {
		Account account = accountService.checkEmailToken(token, email);
		int count = accountService.count();
		
		boolean isCheck = account.getEmail() != null && account.getEmail().equals(email);  
		model.addAttribute("isCheck", isCheck);
		model.addAttribute("count", count);
		model.addAttribute("nickname", account.getNickname());
		
		return "account/confirm-email";
	}
	
	@GetMapping("/resend-email/{nickname}")
	public String resendEmail(@PathVariable String nickname, RedirectAttributes attributes) {
		Account account = accountService.findByNickname(nickname);
		
		if(account.canSendEmail()) {
			accountService.sendEmail(account, "빡공 회원 가입 인증 요청 메일입니다.", "/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
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
	
	@GetMapping("/email-login")
	public String emailLoginForm(Model model) {
		model.addAttribute(new EmailForm());
		return "account/email-login";
	}
	
	@PostMapping("/email-login")
	public String sendEmailLogin(@Valid @ModelAttribute EmailForm emailForm, Errors errors, RedirectAttributes attribute) {
		if(errors.hasErrors()) {
			return "account/email-login";
		}
		
		if(accountService.emailLogin(emailForm)) {
			attribute.addFlashAttribute("message", "이메일 로그인 링크를 메일로 전송했습니다.");
		} else {
			attribute.addFlashAttribute("failMessage", "이메일 로그인 링크를 메일로 전송하지 못했습니다.");
		}
		
		
		return "redirect:/email-login";
	}
	
	@GetMapping("/login-by-email")
	public String emailLogin(@RequestParam String token, @RequestParam String email, Model model) {
		Account account = accountService.checkEmailToken(token, email);
		model.addAttribute(account);
		return "redirect:/";
	}
}
