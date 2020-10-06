package com.study.modules.account;

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

import com.study.modules.account.form.EmailForm;
import com.study.modules.account.form.SignUpForm;
import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;

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
		this.sendEmail(reAccount, "빡공 회원 가입 인증 요청 메일입니다.", "/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
		return reAccount;
	}
	
	public boolean emailLogin(EmailForm emailForm) {
		Account account = accountRepository.findByEmail(emailForm.getEmail()).orElseGet(Account :: new);
		
		if(!account.canSendEmail()) {
			return false;
		}
		
		this.sendEmail(account, "빡공 로그인 링크입니다.", "/login-by-email?token=" + account.getEmailCheckToken() +"&email=" + account.getEmail());
		return true;
	}
	
	public void sendEmail(Account account, String subject, String text) {
		// 메일 전송 시간 저장 -> 1시간에 한번씩만 전송 가능
		account.setSendEmailAt(LocalDateTime.now());
		
		// SimpleMailMessage로 전송 -> ConsoleMailMessage
		SimpleMailMessage simpleMessage = new SimpleMailMessage();
		simpleMessage.setFrom("admin@studyhard.com");
		simpleMessage.setTo(account.getEmail());
		simpleMessage.setSubject(subject);
		simpleMessage.setText(text);
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
	
	public Account checkEmailToken(String token, String email) {
		Account account = accountRepository.findByEmail(email).orElseGet(Account :: new);		
		boolean isCheck = account.getEmailCheckToken() != null && account.getEmailCheckToken().equals(token);
		if(isCheck) {
			account.setEmailVerified(true);
			account.setJoinedAt(LocalDateTime.now());
			this.login(account);
		}
		
		return account;
	}
	
	public int count() {
		return (int)accountRepository.count();
	}
	
	public Account findByNickname(String nickname) {
		return accountRepository.findByNickname(nickname).orElseGet(Account :: new);
	}
	
	public Account findAccountWithTagsById(Account account) {
		return accountRepository.findAccountWithTagsById(account.getId());
	}
	
	public Account findAccountWithZonesById(Account account) {
		return accountRepository.findAccountWithZonesById(account.getId());
	}
	
	public void addTag(Account account, Tag tag) {
		Account byId = this.findById(account);
		byId.addTag(tag);
	}

	public void removeTag(Account account, Tag tag) {
		Account byId = this.findById(account);
		byId.removeTag(tag);
	}

	public void addZone(Account account, Zone zone) {
		Account byId = this.findById(account);
		byId.addZone(zone);		
	}
	
	public void removeZone(Account account, Zone zone) {
		Account byId = this.findById(account);
		byId.removeZone(zone);
	}
	
	private Account findById(Account account) {
		return accountRepository.findById(account.getId()).orElseThrow(()-> new IllegalArgumentException());
	}
}
