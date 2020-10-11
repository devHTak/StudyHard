package com.study.modules.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.form.NicknameForm;
import com.study.modules.account.form.NotificationForm;
import com.study.modules.account.form.PasswordForm;
import com.study.modules.account.form.ProfileForm;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class SettingsService {

	private final AccountRepository accountRepository;
	private final AccountService accountService;
	private final PasswordEncoder passwordEncoder;
	
	public Account updateProfile(Account account, ProfileForm profileForm) {
		account.setBio(profileForm.getBio());
		account.setUrl(profileForm.getUrl());
		account.setLocation(profileForm.getLocation());
		account.setOccupation(profileForm.getOccupation());
		account.setProfileImage(profileForm.getProfileImage());
		
		return accountRepository.save(account);
	}
	
	public Account updatePassword(Account account, PasswordForm passwordForm) {
		account.setPassword(passwordEncoder.encode(passwordForm.getPassword()));
		
		return accountRepository.save(account);
	}
	
	public Account updateNotification(Account account, NotificationForm notificationForm) {
		account.setStudyCreatedByEmail(notificationForm.isStudyCreatedByEmail());
		account.setStudyCreatedByWeb(notificationForm.isStudyCreatedByWeb());
		account.setStudyEnrollmentResultByEmail(notificationForm.isStudyEnrollmentResultByEmail());
		account.setStudyEnrollmentResultByWeb(notificationForm.isStudyEnrollmentResultByWeb());
		account.setStudyUpdatedByWeb(notificationForm.isStudyUpdatedByWeb());
		account.setStudyUpdatedByEmail(notificationForm.isStudyUpdatedByEmail());
		
		return accountRepository.save(account);
	}
	
	public Account updateNickname(Account account, NicknameForm nicknameForm) {
		account.setNickname(nicknameForm.getNickname());
		
		accountService.login(account);
		return accountRepository.save(account);
	}
}
