package com.study.modules.study;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.study.form.DescriptionForm;
import com.study.modules.study.form.PathForm;
import com.study.modules.study.form.StudyForm;
import com.study.modules.study.form.TitleForm;
import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class StudyService {
	private final StudyRepository studyRepository;
	
	public boolean existsByPath(String path) {
		return studyRepository.existsByPath(path);
	}
	
	public Study save(StudyForm studyForm, Account account) {
		Study study = Study.builder()
				.fullDescription(studyForm.getFullDescription())
				.path(studyForm.getPath())
				.title(studyForm.getTitle())
				.createdDateTime(LocalDateTime.now())
				.shortDescription(studyForm.getShortDescription()).build();
		study.addManager(account);
		
		return studyRepository.save(study);
	}
	
	public Study getOnlyStudyByPath(String path) {
		return studyRepository.findOnlyStudyByPath(path).orElseThrow(() -> new IllegalArgumentException(path +"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyByPath(String path) {
		return studyRepository.findByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyWithManagerByPath(String path) {
		return studyRepository.findStudyWithManagerByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyWithTagsAndManagersByPath(String path) {
		return studyRepository.findStudyWithTagsAndManagersByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyWithZonesAndManagersByPath(String path) {
		return studyRepository.findStudyWithZonesAndManagersByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyWithMembersAndManagersByPath(String path) {
		return studyRepository.findStudyWithMembersAndManagersByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyToUpdate(String path, Account account) {
		Study study = this.getStudyByPath(path);
		
		checkIfManager(account, study);
		return study;
	}
	
	public Study getStudyWithManagerToUpdate(String path, Account account) {
		Study study = this.getStudyWithManagerByPath(path);
		checkIfManager(account, study);
		
		return study;
	}
	
	public Study getStudyWithTagsToUpdate(String path, Account account) {
		Study study = this.getStudyWithTagsAndManagersByPath(path);
		checkIfManager(account, study);
		
		return study;
	}
	
	public Study getStudyWithZonesToUpdate(String path, Account account) {
		Study study = this.getStudyWithZonesAndManagersByPath(path);
		checkIfManager(account, study);
		
		return study;
	}
	
	public void checkIfManager(Account account, Study study) {
		if(!study.getManagers().contains(account)) {
			throw new IllegalArgumentException("매니저만 수정 가능합니다.");
		}
	}
	
	public void updateDescription(Study study, DescriptionForm descriptionForm) {
		study.setShortDescription(descriptionForm.getShortDescription());
		study.setFullDescription(descriptionForm.getFullDescription());
	}
	
	public void updateUseBanner(Study study, boolean useBanner) {
		study.setUseBanner(useBanner);
	}
	
	public void updateBanner(Study study, String image) {
		study.setImage(image);
	}

	public void addTag(Study study, Tag tag) {
		if(!study.getTags().contains(tag)) {
			study.getTags().add(tag);
		}
	}
	
	public void removeTag(Study study, Tag tag) {
		if(study.getTags().contains(tag)) {
			study.getTags().remove(tag);
		}
	}

	public void addZone(Study study, Zone zone) {
		if(!study.getZones().contains(zone)) {
			study.getZones().add(zone);
		}
	}
	
	public void removeZone(Study study, Zone zone) {
		if(study.getZones().contains(zone)) {
			study.getZones().remove(zone);
		}
	}
	
	public void openStudy(Study study) {
		study.setPublished(true);
		study.setRecruiting(false);
		study.setClosed(false);
		study.setPublishedDateTime(LocalDateTime.now());	
	}
	
	public void closeStudy(Study study) {
		study.setPublished(false);
		study.setRecruiting(false);
		study.setClosed(true);
		study.setClosedDateTime(LocalDateTime.now());
	}
	
	public void recruiteStartStudy(Study study) {
		study.setPublished(true);
		study.setClosed(false);
		study.setRecruiting(true);
		study.setRecruitingUpdatedDateTime(LocalDateTime.now());
	}
	
	public void recruiteStopStudy(Study study) {
		study.setPublished(true);
		study.setClosed(false);
		study.setRecruiting(false);
	}
	
	public void updateStudyPath(Study study, PathForm pathForm) {
		study.setPath(pathForm.getPath());
	}
	
	public void updateStudyTitle(Study study, TitleForm titleForm) {
		study.setTitle(titleForm.getTitle());;
	}
	
	public void deleteByPath(String path) {
		studyRepository.deleteByPath(path);
	}
	
	public void joinStudyMembers(Study study, Account account) {
		if( !study.getManagers().contains(account) && !study.getMembers().contains(account)) {
			study.addMember(account);
		}
	}
	
	public void removeStudyMembers(Study study, Account account) {
		if( !study.getManagers().contains(account) && study.getMembers().contains(account)) {
			study.removeMember(account);
		}
	}
}
