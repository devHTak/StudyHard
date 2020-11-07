package com.study.modules.main;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.study.modules.account.Account;
import com.study.modules.account.AccountService;
import com.study.modules.account.CurrentUser;
import com.study.modules.event.Enrollment;
import com.study.modules.event.EnrollmentRepository;
import com.study.modules.study.Study;
import com.study.modules.study.StudyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final StudyService studyService;
	private final AccountService accountService;
	private final EnrollmentRepository enrollmentRepository;
	
	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if(account != null) {
			Account byId = accountService.findAccountWithTagsAndZonesById(account);
			List<Study> studyManagerOf = studyService.findTop5ByManagersContainingAndClosedOrderByPublishedDateTime(byId, false);
			List<Study> studyMemberOf  = studyService.findTop5ByMembersContainingAndClosedOrderByPublishedDateTime(byId, false);
			List<Study> studyList = studyService.findStudyByTagsAndZones(byId.getTags(), byId.getZones());
			List<Enrollment> enrollmentList = enrollmentRepository.findByAccount(account);
			
			model.addAttribute("account", byId);
			model.addAttribute("studyManagerOf", studyManagerOf);
			model.addAttribute("studyMemberOf", studyMemberOf);
			model.addAttribute("studyList", studyList);
			model.addAttribute("enrollmentList", enrollmentList);
			
			return "index-login";
		} else {
			List<Study> studyList = studyService.findTop5ByPublishedAndClosed(true, false);
			model.addAttribute("studyList", studyList);
			return "index";
		}
	}
	
	@GetMapping("/login")
	public String login() {
		return "account/login";
	}
	
	@PostMapping("/search/study")
	public String searchStudy(@PageableDefault(size = 9, page = 0, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable, String keyword, Model model) {
		Page<Study> studyPage = studyService.searchStudyByKeywordAndPublished(keyword, pageable);
	
		model.addAttribute("studyPage", studyPage);
		model.addAttribute("studyList", studyPage.getContent());
		model.addAttribute("keyword", keyword);
		model.addAttribute("count", studyPage.getContent().size());
		model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
		return "search";
	}

}
