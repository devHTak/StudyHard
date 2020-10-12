package com.study.modules.study.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.study.modules.study.StudyService;
import com.study.modules.study.form.PathForm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PathFormValidator implements Validator {
	
	private final StudyService studyService;

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return PathForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		PathForm pathForm = (PathForm)target;
		
		if(studyService.existsByPath(pathForm.getPath())) {
			errors.rejectValue("path", "duplicate.path", "동일한 경로가 존재합니다.");
		}
	}
	
	

}
