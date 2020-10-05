package com.study.modules.account;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
	private LocalDateTime joinedAt;
	private LocalDateTime sendEmailAt;
	
	private String bio;
	private String url;
	private String occupation;
	private String location;
	
	@Lob @Basic(fetch = FetchType.EAGER)
	private String profileImage;
	
	private LocalDateTime emailCheckTokenGeneratedAt;
	
	private boolean studyCreatedByEmail;
	private boolean studyCreatedByWeb;
	private boolean studyEnrollmentResultByEmail;
	private boolean studyEnrollmentResultByWeb;
	private boolean studyUpdatedByEmail;
	private boolean studyUpdatedByWeb;
	
	@ManyToMany @Default @Basic(fetch = FetchType.LAZY)
	private Set<Tag> tags = new HashSet<>();
	
	@ManyToMany @Default @Basic(fetch = FetchType.LAZY)
	private Set<Zone> zones = new HashSet<>();

	public void generateToken() {
		this.emailCheckTokenGeneratedAt = LocalDateTime.now();
		this.emailCheckToken = UUID.randomUUID().toString();
	}
	
	public boolean canSendEmail() {
		return this.sendEmailAt.minusHours(1).isAfter(LocalDateTime.now());
	}
	
	public boolean isOwner(UserAccount userAccount) {
		Account returnAccount = userAccount.getAccount();
		return this.equals(returnAccount);
	}
	
	public void addTag(Tag tag) {
		if(!this.tags.contains(tag)) {
			this.tags.add(tag);
		}	
	}
	
	public void removeTag(Tag tag) {
		if(this.tags.contains(tag)) {
			this.tags.remove(tag);
		}
	}
	
	public void addZone(Zone zone) {
		if(!this.zones.contains(zone)) {
			this.zones.add(zone);
		}
	}
	
	public void removeZone(Zone zone) {
		if(this.zones.contains(zone)) {
			this.zones.remove(zone);
		}
	}
}
