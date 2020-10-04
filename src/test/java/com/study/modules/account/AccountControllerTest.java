package com.study.modules.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.study.modules.account.form.SignUpForm;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class AccountControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	@MockBean  JavaMailSender javaMailSender;
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}
	
	@DisplayName("회원 가입 폼 확인")
	@Test
	public void signUpFormTest() throws Exception {
		mockMvc.perform(get("/sign-up"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"))
				.andExpect(model().attributeExists("signUpForm"));
	}
	
	@DisplayName("회원 가입 실패")
	@Test
	public void signUpFailTest() throws Exception {
		mockMvc.perform(post("/sign-up")
						.param("nickname", "test")
						.param("email", "test")
						.param("password", "test")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/sign-up"))
				.andExpect(model().hasErrors())
				.andExpect(unauthenticated());
	}
	
	@DisplayName("회원 가입 성공")
	@Test
	public void signUpSuccessTest() throws Exception {
		mockMvc.perform(post("/sign-up")
						.param("nickname", "qkrgusxkr")
						.param("email", "qkrgusxkr1@gmail.com")
						.param("password", "12341234")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(authenticated())
				.andExpect(view().name("redirect:/"));
		
		Account account = accountService.findByNickname("qkrgusxkr");
		assertNotNull(account);
		assertNotNull(account.getEmailCheckToken());
		assertNotEquals(account.getPassword(), "12341234"); //decoding 여부
		
		assertThat(accountService.existsByEmail("qkrgusxkr1@gmail.com"));
		assertThat(accountService.existsByNickname("qkrgusxkr"));
		BDDMockito.then(javaMailSender).should().send(ArgumentMatchers.any(SimpleMailMessage.class));
	}
	
	@DisplayName("이메일 토큰 체크 -실패")
	@Test
	public void checkEmailTokenFailTest() throws Exception {
		mockMvc.perform(get("/check-email-token")
						.param("token", "test")
						.param("nickname", "test"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/confirm-email"))
				.andExpect(model().attribute("isCheck", false));
	}
	
	@DisplayName("이메일 토큰 체크 - 성공")
	@Test
	public void checkEmailTokenSuccessTest() throws Exception {
		SignUpForm signUpForm = SignUpForm.builder()
											.nickname("testtest")
											.email("testtest@gmail.com")
											.password("12341234").build();
		Account account = accountService.signUp(signUpForm);
		
		mockMvc.perform(get("/check-email-token")
						.param("token", account.getEmailCheckToken())
						.param("nickname", account.getNickname()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/confirm-email"))
				.andExpect(model().attribute("isCheck", true))
				.andExpect(model().attributeExists("count"))
				.andExpect(model().attributeExists("nickname"))
				.andExpect(authenticated().withUsername("testtest"));
		
		assertEquals(account.getNickname(), signUpForm.getNickname());
	}	
}
