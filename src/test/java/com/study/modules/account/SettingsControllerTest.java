package com.study.modules.account;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SettingsControllerTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired SettingsService settingsService;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	@Autowired PasswordEncoder passwordEncoder;
	
	@AfterEach
	public void afterEach() {
		accountRepository.deleteAll();
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("프로필 수정 폼 확인")
	@Test
	public void profileFormCheck() throws Exception {
		mockMvc.perform(get("/settings/profile"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("profileForm"))
				.andExpect(view().name("settings/profile"));
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("프로필 수정 - 성공")
	@Test
	public void profileUpdateSuccess() throws Exception {
		mockMvc.perform(post("/settings/profile")
						.param("bio", "test")
						.param("occupation", "개발자")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMsg"))
				.andExpect(redirectedUrl("/settings/profile"));
		
		Account account = accountService.findByNickname("testNickname");
		assertEquals(account.getBio(), "test");
		assertEquals(account.getOccupation(), "개발자");
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("프로필 수정 - 실패")
	@Test
	public void profileUpdateFail() throws Exception {
		mockMvc.perform(post("/settings/profile")
						.param("url", "test")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("profileForm"))
				.andExpect(model().hasErrors())
				.andExpect(view().name("settings/profile"));
		
		Account account = accountService.findByNickname("testNickname");
		assertNotEquals(account.getUrl(), "test");
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("패스워드 폼 확인")
	@Test
	public void passwordFormCheck() throws Exception {
		mockMvc.perform(get("/settings/password"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("settings/password"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("passwordForm"));						
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("패스워드 수정 - 성공")
	@Test
	public void passwordUpdateSuccess() throws Exception {
		mockMvc.perform(post("/settings/password")
						.param("password", "123412345")
						.param("rePassword", "123412345")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMsg"))
				.andExpect(redirectedUrl("/settings/password"));
		
		Account account = accountService.findByNickname("testNickname");
		assertTrue(passwordEncoder.matches("123412345", account.getPassword()));
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("패스워드 수정 - 실패")
	@Test
	public void passwordUpdateFail() throws Exception {
		mockMvc.perform(post("/settings/password")
						.param("password", "123412345")
						.param("rePassword", "12341234")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().hasErrors())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("passwordForm"))
				.andExpect(view().name("settings/password"));
		
		Account account = accountService.findByNickname("testNickname");
		assertFalse(passwordEncoder.matches("123412345", account.getPassword()));
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("알림변경 폼 확인")
	@Test
	public void notificationFormCheck() throws Exception {
		mockMvc.perform(get("/settings/notification"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("settings/notification"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("notificationForm"));
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("알림 변경 수정 - 성공")
	@Test
	public void notificationUpdateSuccess() throws Exception {
		mockMvc.perform(post("/settings/notification")
						.param("studyCreatedByEmail", "true")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMsg"))
				.andExpect(redirectedUrl("/settings/notification"));
		
		Account account = accountService.findByNickname("testNickname");
		assertEquals(account.isStudyCreatedByEmail(), true);
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("닉네임 변경 폼 확인")
	@Test
	public void accountFormCheck() throws Exception {
		mockMvc.perform(get("/settings/account"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("settings/account"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("nicknameForm"));
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("닉네임 변경 수정 - 성공")
	@Test
	public void nicknameUpdateSuccess() throws Exception {
		mockMvc.perform(post("/settings/account")
						.param("nickname", "reTestNickname")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMsg"))
				.andExpect(redirectedUrl("/settings/account"));
		
		Account account = accountService.findByNickname("reTestNickname");
	}
	
	@WithAccount(value = "testNickname")
	@DisplayName("닉네임 변경 수정 - 실패")
	@Test
	public void nicknameUpdateFail() throws Exception {
		mockMvc.perform(post("/settings/account")
						.param("nickname", "nicknameIsToooooooooLong")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("nicknameForm"))
				.andExpect(model().hasErrors())
				.andExpect(view().name("settings/account"));
		
		Account account = accountService.findByNickname("testNickname");
	}

}
