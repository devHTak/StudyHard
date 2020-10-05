package com.study.modules.account.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class EmailForm {
	
	@NotBlank 
	@Email
	private String email;

}
