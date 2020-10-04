package com.study.modules.account.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfileForm {
	
	@Length(min = 2, max = 30)
	private String bio;
	
	@URL
	private String url;
	
	@Length(min = 2, max = 30)
	private String occupation;
	
	@Length(min = 2, max = 30)
	private String location;
	
	private String profileImage;
}
