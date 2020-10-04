package com.study.modules.account;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Table(name="PERSISTENT_LOGINS")
@Entity
@Getter @Setter
public class PersistentLogins {
	
	@Id
	@Column(length = 64)
	private String series;
	
	@Column(nullable = true, length = 64)
	private String username;
	
	@Column(nullable = false, length = 64)
	private String token;
	
	@Column(name = "last_used", nullable = true, length = 64)
	private LocalDateTime lastUsed;

}
