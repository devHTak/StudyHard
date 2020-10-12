package com.study.modules.study.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TitleForm {
	
	@NotBlank 
	@Length(max = 50)
	private String title; 

}
