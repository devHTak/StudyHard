package com.study.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.account.AccountService;
import com.study.modules.account.form.EmailForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailFormValidator implements Validator {
	
	private final AccountService accountService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return EmailForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		EmailForm emailForm = (EmailForm)target;
		
		if(!accountService.existsByEmail(emailForm.getEmail())) {
			errors.rejectValue("email", "wrong.email", "등록된 이메일이 없습니다.");
		}
	}
	
	

}
