package com.study.account;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {
	
	@Id @GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String nickname;
	
	@Column(nullable = false)
	private String password;
	
	private String emailCheckToken;
	
	private boolean emailVerified;
	private LocalDateTime joinAt;
	private LocalDateTime sendEmailAt;
	
	private String bio;
	private String url;
	private String occupation;
	private String location;
	private String profileImage;
	
	private LocalDateTime emailCheckTokenGeneratedAt;
	
	private boolean studyCreatedByEmail;
	private boolean studyCreatedByWeb;
	private boolean studyEnrollmentResultByEmail;
	private boolean studyEnrollmentResultByWeb;
	private boolean studyUpdatedByEmail;
	private boolean studyUpdateByWeb;

	public void generateToken() {
		this.emailCheckTokenGeneratedAt = LocalDateTime.now();
		this.emailCheckToken = UUID.randomUUID().toString();
	}
	
	public boolean canSendEmail() {
		return this.sendEmailAt.minusHours(1).isAfter(LocalDateTime.now());
	}
}
