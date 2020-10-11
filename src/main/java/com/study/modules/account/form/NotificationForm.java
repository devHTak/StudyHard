package com.study.modules.account.form;

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
public class NotificationForm {
	
	private boolean studyCreatedByEmail;
	private boolean studyCreatedByWeb;
	private boolean studyEnrollmentResultByEmail;
	private boolean studyEnrollmentResultByWeb;
	private boolean studyUpdatedByEmail;
	private boolean studyUpdatedByWeb;
	
	public NotificationForm(Account account) {
		this.setStudyCreatedByEmail(account.isStudyCreatedByEmail());
		this.setStudyCreatedByWeb(account.isStudyCreatedByWeb());
		this.setStudyEnrollmentResultByEmail(account.isStudyEnrollmentResultByEmail());
		this.setStudyEnrollmentResultByWeb(account.isStudyEnrollmentResultByWeb());
		this.setStudyUpdatedByEmail(account.isStudyUpdatedByEmail());
		this.setStudyUpdatedByWeb(account.isStudyUpdatedByWeb());
	}

}
