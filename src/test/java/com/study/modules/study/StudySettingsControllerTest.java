package com.study.modules.study;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import com.study.modules.study.form.StudyForm;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class StudySettingsControllerTest {
	
	@Autowired MockMvc mockMvc;
	@Autowired AccountService accountService;
	@Autowired AccountRepository accountRepository;
	@Autowired StudyService studyService;
	@Autowired StudyRepository studyRepository;
	
	public Study createStudy(String path, String nickname) {
		StudyForm studyForm = StudyForm.builder().path(path).title("test").shortDescription("test") .fullDescription("test").build();
		Account account = accountService.findByNickname(nickname);
		
		return studyService.save(studyForm, account);
	}
	
	public Account createAccount(String nickname) {
		Account account = Account.builder().email(nickname + "@test.com").nickname(nickname).password("12341234").build();
		return accountRepository.save(account);
	}
	
	@AfterEach
	public void afterEach() {
		studyRepository.deleteAll();
		accountRepository.deleteAll();
	}
	
	@WithAccount("testNickname")
	@DisplayName("설명 폼 확인")
	@Test
	public void getStudyDescriptionFormTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		mockMvc.perform(get("/study/{path}/settings/description", "test-study"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("study/settings/description"))
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(model().attributeExists("descriptionForm"));
	}
	
	@WithAccount("testNickname")
	@DisplayName("설명 폼 변경 - 성공")
	@Test
	public void updateStudyDescriptionSuccess() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/description", "test-study")
						.param("shortDescription", "modify")
						.param("fullDescription", "testtest")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMessage"))
				.andExpect(redirectedUrl("/study/test-study/settings/description"));
		
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertTrue(study.getShortDescription().equals("modify"));
		assertTrue(study.getFullDescription().equals("testtest"));
	}
	
	@WithAccount("testNickname")
	@DisplayName("배너 수정 폼 확인")
	@Test
	public void getStudyBannerFormTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		mockMvc.perform(get("/study/{path}/settings/banner", "test-study"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(view().name("study/settings/banner"));
	}
	
	@WithAccount("testNickname")
	@DisplayName("배너 사용여부 변경 - 사용에서 미사용")
	@Test
	public void notUseStudyBannerTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		mockMvc.perform(post("/study/{path}/settings/useBanner", "test-study")
						.param("useBanner", "false")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMessage"))
				.andExpect(redirectedUrl("/study/test-study/settings/banner"));
		
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertFalse(study.isUseBanner());
	}
	
	@WithAccount("testNickname")
	@DisplayName("배너 사용여부 변경 - 미사용에서 사용")
	@Test
	public void useStudyBannerTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");

		mockMvc.perform(post("/study/{path}/settings/useBanner", "test-study")
						.param("useBanner", "true")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(flash().attributeExists("updateMessage"))
				.andExpect(redirectedUrl("/study/test-study/settings/banner"));
		
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertTrue(study.isUseBanner());
	}
	
	// TODO TAG, ZONE 관련 테스트 코드 작성 필요
	
	@WithAccount("testNickname")
	@DisplayName("스터디 설정 변경 폼 확인 ")
	@Test
	public void getStudySettingsFormCheck() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(get("/study/{path}/settings/study", "test-study"))
				.andDo(print())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(model().attributeExists("pathForm"))
				.andExpect(model().attributeExists("titleForm"))
				.andExpect(status().isOk())
				.andExpect(view().name("study/settings/study"));
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 오픈 확인")
	@Test
	public void openStudyTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/open", "test-study")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/test-study/settings/study"));
	
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertTrue(study.isPublished());
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 종료 확인")
	@Test
	public void closeStudyTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/close", "test-study")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/test-study/settings/study"));
	
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertTrue(study.isClosed());
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 모임 시작 확인")
	@Test
	public void recruiteStartStudyTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/recruite/start", "test-study")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/test-study/settings/study"));
	
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertTrue(study.isRecruiting());
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 모임 종료 확인")
	@Test
	public void recruiteStopStudyTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/recruite/stop", "test-study")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/test-study/settings/study"));
	
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertFalse(study.isRecruiting());
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 제목 수정 - 실패")
	@Test
	public void updateStudyTitleFailTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/title", "test-study")
						.param("title", "long-title-test-long-title-test-long-title-test-long-title-test-long-title-test-long-title-test")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(model().attributeExists("pathForm"))
				.andExpect(view().name("study/settings/study"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeHasFieldErrors("titleForm", "title"));
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 제목 수정 - 성공")
	@Test
	public void updateStudyTitleSuccessTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/title", "test-study")
						.param("title", "testTitle")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/test-study/settings/study"));
	
		Account account = accountService.findByNickname("testNickname");
		assertTrue(study.getManagers().contains(account));
		assertEquals("testTitle", study.getTitle());
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 경로 수정 - 실패")
	@Test
	public void updateStudyPathFailTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/path", "test-study")
						.param("path", "long-path-test-long-path-test-long-path-test-long-path-test")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("account"))
				.andExpect(model().attributeExists("study"))
				.andExpect(model().attributeExists("titleForm"))
				.andExpect(view().name("study/settings/study"))
				.andExpect(model().hasErrors())
				.andExpect(model().attributeHasFieldErrors("pathForm", "path"));
	}
	
	@WithAccount("testNickname")
	@DisplayName("스터디 경로 수정 - 성공")
	@Test
	public void updateStudyPathSuccessTest() throws Exception {
		Study study = this.createStudy("test-study", "testNickname");
		
		mockMvc.perform(post("/study/{path}/settings/study/path", "test-study")
						.param("path", "re-test-study")
						.with(csrf()))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/study/re-test-study/settings/study"));
	
		Account account = accountService.findByNickname("testNickname");
		Study reStudy = studyService.getStudyWithManagerByPath("re-test-study");
		assertTrue(reStudy.getManagers().contains(account));
		assertEquals("re-test-study", reStudy.getPath());
	}	
}
