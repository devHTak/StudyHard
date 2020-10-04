package com.study.modules.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.form.NotificationForm;
import com.study.modules.account.form.PasswordForm;
import com.study.modules.account.form.ProfileForm;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class SettingsService {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	
	public Account updateProfile(Account account, ProfileForm profileForm) {
		Account byId = this.findById(account);
		
		byId.setBio(profileForm.getBio());
		byId.setUrl(profileForm.getUrl());
		byId.setLocation(profileForm.getLocation());
		byId.setOccupation(profileForm.getOccupation());
		byId.setProfileImage(profileForm.getProfileImage());
		
		return byId;
	}
	
	public Account updatePassword(Account account, PasswordForm passwordForm) {
		Account byId = this.findById(account);
		byId.setPassword(passwordEncoder.encode(passwordForm.getPassword()));
		
		return byId;
	}
	
	public Account updateNotification(Account account, NotificationForm notificationForm) {
		Account byId = this.findById(account);
		
		byId.setStudyCreatedByEmail(notificationForm.isStudyCreatedByEmail());
		byId.setStudyCreatedByWeb(notificationForm.isStudyCreatedByWeb());
		byId.setStudyEnrollmentResultByEmail(notificationForm.isStudyEnrollmentResultByEmail());
		byId.setStudyEnrollmentResultByWeb(notificationForm.isStudyEnrollmentResultByWeb());
		byId.setStudyUpdatedByWeb(notificationForm.isStudyUpdatedByWeb());
		byId.setStudyUpdatedByEmail(notificationForm.isStudyUpdatedByEmail());
		return byId;
	}
	
	private Account findById(Account account) {
		return accountRepository.findById(account.getId())
				.orElseThrow(()-> new IllegalArgumentException(account.getNickname()+"에 대한 계정을 찾을 수 없습니다."));
	}
}
