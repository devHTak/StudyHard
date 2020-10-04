package com.study.modules.account.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.account.AccountService;
import com.study.modules.account.form.SignUpForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator{
	
	private final AccountService accountService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return SignUpForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		SignUpForm signUpForm = (SignUpForm)target;
		
		if(accountService.existsByNickname(signUpForm.getNickname())) {
			errors.rejectValue("nickname", "닉네임 중복", new Object[] {signUpForm.getNickname()}, "동일한 닉네임이 존재합니다.");
		}
		if(accountService.existsByEmail(signUpForm.getEmail())) {
			errors.rejectValue("email", "이메일 중복", new Object[] {signUpForm.getEmail()}, "동일한 이메일이 존재합니다.");
		}
	}
	
	

}
