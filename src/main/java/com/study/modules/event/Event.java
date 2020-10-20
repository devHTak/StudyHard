package com.study.modules.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.study.modules.account.Account;
import com.study.modules.account.UserAccount;
import com.study.modules.study.Study;

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
public class Event {
	
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne 
	private Study study;
	
	@ManyToOne
	private Account createdBy;
	
	@Column(nullable = false)
	private String title;
	
	@Lob
	private String description;
	
	@Column(nullable = false)
	private LocalDateTime createDateTime;
	@Column(nullable = false)
	private LocalDateTime endEnrollmentDateTime;
	@Column(nullable = false)
	private LocalDateTime startDateTime;
	@Column(nullable = false)
	private LocalDateTime endDateTime;
	
	@Column(nullable = false)
	private Integer limitOfEnrollments;
	
	@OneToMany(mappedBy = "event") @Builder.Default
	private List<Enrollment> enrollments = new ArrayList<>();
	
	@Enumerated(value = EnumType.STRING)
	private EventType eventType;
	
	public int numberOfRemainSpots() {
		return this.limitOfEnrollments - this.enrollments.size();
	}
	
	public boolean isEnrollableFor(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		
		return !this.isEndEnrollment() && !this.isAttended(account)  && !this.isAlreadyEnrolled(account);
	}
	
	public boolean isDisenrollableFor(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		
		return !this.isEndEnrollment() && !this.isAttended(account) && this.isAlreadyEnrolled(account);
	}
	
	public boolean isEndEnrollment() {
		return endEnrollmentDateTime.isBefore(LocalDateTime.now());
	}
	
	public boolean isAttended(Account account) {
		
		for(Enrollment enrollment : this.enrollments) {
			if(enrollment.getAccount().equals(account) && enrollment.getAttended()) {
				return true;
			}
		}
		
		return false;
	}
	public boolean isAttended(UserAccount userAccount) {
		Account account = userAccount.getAccount();
		return this.isAttended(account);
	}
	
	public boolean isAlreadyEnrolled(Account account) {
		
		for(Enrollment enrollment : this.enrollments) {
			if(enrollment.getAccount().equals(account)) {
				return true;
			}
		}
		
		return false;
	}

}
 