package com.study.account;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.study.account.form.SignUpForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender javaMailSender;
	
	public Account signUp(SignUpForm signUpForm) {
		Account account = Account.builder()
								.nickname(signUpForm.getNickname())
								.email(signUpForm.getEmail())
								.password(passwordEncoder.encode(signUpForm.getPassword()))
								.build();
		
		Account reAccount = accountRepository.save(account);
		reAccount.generateToken();
		
		// 메일 보내기
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setFrom("admin@studyhard.com");
		simpleMessage.setTo(reAccount.getEmail());
		simpleMessage.setSubject("빡공 회원 가입 인증 요청 메일입니다.");
		simpleMessage.setText("/check-email-token?token=" + reAccount.getEmailCheckToken() + "&nickname=" + reAccount.getNickname() );
		javaMailSender.send(simpleMessage);
		
		return reAccount;
	}
	
	public void login(Account account) {
		Collection authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account.getNickname(), account.getPassword(), authorities);
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(token);
	}

	public boolean existsByNickname(String nickname) {
		return accountRepository.existsByNickname(nickname);
	}
	
	public boolean existsByEmail(String email) {
		return accountRepository.existsByEmail(email);
	}
}
