package com.study.modules.account.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.study.modules.account.Account;

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
	
	@Length(max = 30)
	private String bio;
	
	@URL
	private String url;
	
	@Length(max = 30)
	private String occupation;
	
	@Length(max = 30)
	private String location;
	
	private String profileImage;
	
	public ProfileForm(Account account) {
		this.setBio(account.getBio());
		this.setLocation(account.getLocation());
		this.setOccupation(account.getOccupation());
		this.setUrl(account.getUrl());
		this.setProfileImage(account.getProfileImage());
	}
}
