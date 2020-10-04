package com.study.modules.account.form;

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
public class PasswordForm {
	
	@NotBlank
	@Length(min = 8, max = 20)
	private String password;
	
	@NotBlank
	@Length(min = 8, max = 20)
	private String rePassword;

}
