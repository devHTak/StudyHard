package com.study.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.account.AccountService;
import com.study.modules.account.form.NicknameForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator {
	
	private final AccountService accountService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return NicknameForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		NicknameForm nicknameForm = (NicknameForm)target;
		
		if(accountService.existsByNickname(nicknameForm.getNickname())) {
			errors.rejectValue("nickname", "wrong.duplicate.nickname", "동일한 닉네임이 존재합니다.");
		}
	}
	
	

}
