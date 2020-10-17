package com.study.modules.study;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.account.AccountRepository;
import com.study.modules.account.AccountService;
import com.study.modules.account.WithAccount;
import com.study.modules.account.form.SignUpForm;
import com.study.modules.study.form.StudyForm;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class StudyControllerTest {

	@Autowired MockMvc mockMvc;
	@Autowired StudyService studyService;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	@Autowired StudyRepository studyRepository;
	
	@AfterEach
	public void afterEach() {
		accountRepository.deleteAll();
		studyRepository.deleteAll();
	}
	
	@WithAccount("testNickname")
	@DisplayName("신규 스터디 생성 폼 확인")
	@Test
	public void getNewStudyFormTest() throws Exception {
		mockMvc.perform(get("/study/new-study"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("studyForm"))
				.andExpect(view().name("study/form"));						
	}
	
	@WithAccount("testNickname")
	@DisplayName("신규 스터디 생성 - 성공")
	@Test
	public void newStudySuccess() throws Exception {
		mockMvc.perform(post("/study/new-study")
						.param("path", "study-test")
						.param("title", "test")
						.param("shortDescription", "test")
						.param("fullDescription", "testtest")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/study-test"));
		
		Study study = studyService.getStudyByPath("study-test");
		assertNotNull(study);
	}
	
	@WithAccount("testNickname")
	@DisplayName("신규 스터디 생성 - 실패")
	@Test
	public void newStudyFail() throws Exception {
		mockMvc.perform(post("/study/new-study")
						.param("path", "study-test-long-study-test-long-study-test-long-study-test-long")
						.param("title", "test")
						.param("shortDescription", "test")
						.param("fullDescription", "testttest")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeHasFieldErrors("studyForm", "path"))
				.andExpect(view().name("study/form"));
	}
	
	private Study insertStudy(String path) {
		StudyForm studyForm = StudyForm.builder().path(path).title("test").shortDescription("TEST").fullDescription("test").build();
		Account account = accountService.findByNickname("testNickname");
		
		return studyService.save(studyForm, account);
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 조회 화면")
	@Test
	public void getStudyInfoTest() throws Exception {
		Study study = this.insertStudy("test-study");
		
		mockMvc.perform(get("/study/" + study.getEncodePath()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(view().name("study/view"));
		
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 멤버 조회 화면")
	@Test
	public void getStudyMembersTest() throws Exception {
		Study study = this.insertStudy("test-study");
		
		mockMvc.perform(get("/study/" + study.getEncodePath() +"/members"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(view().name("study/members"));
		
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
	}
	
	private Study insertStudyToMember(String path) {
		StudyForm studyForm = StudyForm.builder().path(path).title("test").shortDescription("TEST").fullDescription("test").build();
		Account manager = Account.builder().email("manager@test.com").nickname("manager").password("12341234").build();
		
		Account account = accountRepository.save(manager);
		return studyService.save(studyForm, account);
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 회원 등록 - 성공")
	@Test
	public void joinStudyMembersTest() throws Exception {
		Study study = this.insertStudyToMember("test-study");
		
		mockMvc.perform(post("/study/" + study.getEncodePath() + "/join")
						.param("path", study.getEncodePath())
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/" + study.getEncodePath()));
		
		Account manager = accountService.findByNickname("manager");
		Account account = accountService.findByNickname("testNickname");
		
		assertTrue(study.getManagers().contains(manager));
		assertTrue(study.getMembers().contains(account));
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 회원 탈퇴 - 성공")
	@Test
	public void leaveStudyMembersTest() throws Exception {
		Study study = this.insertStudyToMember("test-study");
		Account member = accountService.findByNickname("testNickname");
		study.addMember(member);
		
		mockMvc.perform(post("/study/" + study.getEncodePath() +"/leave")
						.param("path", study.getEncodePath())
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/" + study.getEncodePath()));
		
		Account manager = accountService.findByNickname("manager");
		
		assertTrue(study.getManagers().contains(manager));
		assertFalse(study.getMembers().contains(member));
	}
}
