package com.study.main;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.study.account.Account;

public class UserAccount extends User{
	
	private Account account;
	
	public UserAccount(Account account) {
		super(account.getNickname(), account.getPassword(), getAuthority());
		this.account = account;
	}
	
	public static Collection getAuthority() {
		Collection authorities = new ArrayList();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return authorities;
	}
	
	public Account getAccount() {
		return this.account;
	}
}
