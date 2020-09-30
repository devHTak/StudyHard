package com.study.account.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SignUpForm {
	
	@NotBlank
	@Length(min = 2, max = 20)
	private String nickname;
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	@Length(min = 8, max = 20)
	private String password;

}
