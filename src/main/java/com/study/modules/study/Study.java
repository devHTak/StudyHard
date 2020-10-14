package com.study.modules.study;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import com.study.modules.account.Account;
import com.study.modules.account.UserAccount;
import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Slf4j
@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Study {
	
	@Id @GeneratedValue
	private Long id;
	
	private String title;
	private String shortDescription;
	private String path;
	
	@Lob @Basic(fetch = FetchType.EAGER)
	private String fullDescription;
	private boolean useBanner;	
	
	@Lob @Basic(fetch = FetchType.EAGER)
	private String image;
	
	@ManyToMany
	@Default @Basic(fetch = FetchType.LAZY)
	private Set<Account> members = new HashSet<>();
	@ManyToMany
	@Default @Basic(fetch = FetchType.LAZY)
	private Set<Account> managers = new HashSet<>();
	
	@ManyToMany
	@Default @Basic(fetch = FetchType.LAZY)
	private Set<Zone> zones = new HashSet<>();
	@ManyToMany
	@Default @Basic(fetch = FetchType.LAZY)
	private Set<Tag> tags = new HashSet<>();
	
	private LocalDateTime createdDateTime;
	
	private boolean published;
	private LocalDateTime publishedDateTime;
	
	private boolean recruiting;
	private LocalDateTime recruitingUpdatedDateTime;
	
	private boolean closed;
	private LocalDateTime closedDateTime;
	
	public String getEncodePath() {
		String url = "";
		try {
			
			url = URLEncoder.encode(this.path, "UTF-8");
		}catch(UnsupportedEncodingException e) {
			log.error(this.path + " encode 과정에서 오류 발생");
		}
		return url;
	}
	
	public void addManager(Account account) {
		if(!this.managers.contains(account)) {
			this.managers.add(account);
		}
	}
	
	public void removeManager(Account account) {
		if(!this.managers.contains(account)) {
			this.managers.remove(account);
		}
	}
	
	public void addMember(Account account) {
		if(!this.members.contains(account)) {
			this.members.add(account);
		}
	}
	
	public void removeMember(Account account) {
		if(this.members.contains(account)) {
			this.members.remove(account);
		}
	}
	
	public boolean isManagers(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		return this.managers.contains(account);
	}
	
	public boolean isMembers(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		return this.members.contains(account);
	}
	
	public boolean isJoinable(UserAccount userAccount) {
		return this.isPublished() && !this.isClosed() && this.isRecruiting()
				&& !this.isManagers(userAccount) && !this.isMembers(userAccount);
	}
	
	
	public boolean isRemovable() {
		return !this.isPublished();
	}
}
