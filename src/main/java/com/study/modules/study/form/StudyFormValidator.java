package com.study.modules.study.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.study.StudyService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator{
	
	private final StudyService studyService;

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return StudyForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		StudyForm studyForm = (StudyForm)target;
		
		if(studyService.existsByPath(studyForm.getPath())) {
			errors.rejectValue("path", "duplicate.path", "동일한 경로가 존재합니다. URL은 중복되면 안됩니다.");
		}
	}
	
	

}
