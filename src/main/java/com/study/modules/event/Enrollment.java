package com.study.modules.event;

import java.time.LocalDateTime;

import javax.persistence.Entity;
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
public class Enrollment {
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Event event;
	
	@ManyToOne
	private Account account;
	
	private LocalDateTime enrolledAt;
	
	private Boolean accepted;
	
	private Boolean attended;

}
