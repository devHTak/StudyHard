package com.study.modules.account;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.study.modules.account.form.SignUpForm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
	
	private final AccountService accountService;

	@Override
	public SecurityContext createSecurityContext(WithAccount withAccount) {
		// TODO Auto-generated method stub
		String nickname = withAccount.value();
		SignUpForm signUpForm = SignUpForm.builder().email("test@test.com").nickname(nickname).password("12341234").build();
		Account account = accountService.signUp(signUpForm);
		
		UserDetails userDetails = accountService.loadUserByUsername(account.getNickname());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		
		return context;
	}
	
	

}
