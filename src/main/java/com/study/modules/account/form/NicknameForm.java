package com.study.modules.account.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

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
public class NicknameForm {
	
	@NotBlank
	@Length(min = 2, max = 20)
	private String nickname;
	
	public NicknameForm(Account account) {
		this.setNickname(account.getNickname());
	}

}
