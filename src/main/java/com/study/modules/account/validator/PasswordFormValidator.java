package com.study.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.account.form.PasswordForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return PasswordForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		PasswordForm passwordForm = (PasswordForm)target;
		
		if(!passwordForm.getPassword().equals(passwordForm.getRePassword())) {
			errors.rejectValue("rePassword", "wrong.password", "패스워드가 일치하지 않습니다.");
		}
	}
}
