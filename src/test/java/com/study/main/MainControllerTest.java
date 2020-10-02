package com.study.main;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.study.account.AccountService;
import com.study.account.form.SignUpForm;
import com.study.account.AccountRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class MainControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	
	@BeforeEach
	void beforeEach() {
		SignUpForm signUpForm = SignUpForm.builder()
											.nickname("testtest")
											.email("test@test.com")
											.password("12341234").build();
		accountService.signUp(signUpForm);
	}
	
	@AfterEach
	void afterEach() {
		accountRepository.deleteAll();
	}
	
	@DisplayName("홈 화면 접속")
	@Test
	public void homeTest() throws Exception {
		mockMvc.perform(get("/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
	}
	
	@DisplayName("로그인 화면 접속")
	@Test
	public void loginFormTest() throws Exception {
		mockMvc.perform(get("/login"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("account/login"));
	}
	
	@DisplayName("이메일 로그인 테스트")
	@Test
	public void loginByEmailTest() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "test@test.com")
						.param("password", "12341234")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("testtest"));
	}
	
	@DisplayName("닉네임 로그인 테스트")
	@Test
	public void loginByNicknameTest() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "testtest")
						.param("password", "12341234")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(authenticated().withUsername("testtest"));
	}
	
	@DisplayName("로그인 테스트 실패")
	@Test
	public void loginFailTest() throws Exception {
		mockMvc.perform(post("/login")
						.param("username", "test")
						.param("password", "1234")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/login?error"))
				.andExpect(unauthenticated());
	}
	
	@WithMockUser
	@DisplayName("로그아웃")
	@Test
	public void logoutTest() throws Exception {
		mockMvc.perform(post("/logout")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"))
				.andExpect(unauthenticated());
	}
}
