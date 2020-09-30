package com.study.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.study.account.form.SignUpForm;
import com.study.main.UserAccount;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService{
	
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
		this.sendEmail(reAccount);
		return reAccount;
	}
	
	public void sendEmail(Account account) {
		// 메일 전송 시간 저장 -> 1시간에 한번씩만 전송 가능
		account.setSendEmailAt(LocalDateTime.now());
		
		// SimpleMailMessage로 전송 -> ConsoleMailMessage
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setFrom("admin@studyhard.com");
		simpleMessage.setTo(account.getEmail());
		simpleMessage.setSubject("빡공 회원 가입 인증 요청 메일입니다.");
		simpleMessage.setText("/check-email-token?token=" + account.getEmailCheckToken() + "&nickname=" + account.getNickname() );
		javaMailSender.send(simpleMessage);
	}
	
	public void login(Account account) {
		Collection authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account), account.getPassword(), authorities);
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(token);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		if(!accountRepository.existsByNickname(username) && !accountRepository.existsByEmail(username))
			throw new UsernameNotFoundException("로그인하지 못하였습니다.");
		
		Account account = accountRepository.findByNickname(username).orElseGet(Account :: new);
		if(account.getNickname() == null && !username.equals(account.getNickname())) {
			account = accountRepository.findByEmail(username).get();
		}
		
		return new UserAccount(account);
	}

	public boolean existsByNickname(String nickname) {
		return accountRepository.existsByNickname(nickname);
	}
	
	public boolean existsByEmail(String email) {
		return accountRepository.existsByEmail(email);
	}
	
	public boolean checkEmailToken(String token, String nickname) {
		Account account = accountRepository.findByNickname(nickname).orElseGet(Account :: new);		
		boolean isCheck = account.getEmailCheckToken() != null && account.getEmailCheckToken().equals(token);
		if(isCheck) {
			account.setEmailVerified(true);
			account.setJoinAt(LocalDateTime.now());
			this.login(account);
		}
		
		return isCheck;
	}
	
	public int count() {
		return (int)accountRepository.count();
	}
	
	public Account findByNickname(String nickname) {
		return accountRepository.findByNickname(nickname).orElseGet(Account :: new);
	}	
}
