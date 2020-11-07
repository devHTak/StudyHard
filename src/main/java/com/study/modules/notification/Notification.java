package com.study.modules.notification;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.study.modules.account.Account;

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
public class Notification {
	
	@Id @GeneratedValue
	private Long id;
	
	private String title;
	
	private String link;
	
	private String message;
	
	private boolean checked;
	
	@ManyToOne
	private Account account;
	
	private LocalDateTime createdAt;
	
	@Enumerated(value = EnumType.STRING)
	private NotificationType notificationType;
	
	

}
