package com.study.modules.study;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;
import com.study.modules.study.form.DescriptionForm;
import com.study.modules.study.form.StudyForm;
import com.study.modules.tag.Tag;
import com.study.modules.tag.TagForm;
import com.study.modules.tag.TagRepository;
import com.study.modules.zone.Zone;
import com.study.modules.zone.ZoneRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class StudyService {
	private final StudyRepository studyRepository;
	private final TagRepository tagRepository;
	private final ZoneRepository zoneRepository;
	
	public boolean existsByPath(String path) {
		return studyRepository.existsByPath(path);
	}
	
	public Study save(StudyForm studyForm, Account account) {
		Study study = Study.builder()
				.fullDescription(studyForm.getFullDescription())
				.path(studyForm.getPath())
				.title(studyForm.getTitle())
				.shortDescription(studyForm.getShortDescription()).build();
		study.addManager(account);
		
		return studyRepository.save(study);
	}
	
	public Study getStudyByPath(String path) {
		return studyRepository.findByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getOnlyStudyByPath(String path) {
		return studyRepository.findOnlyStudyByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyWithTagsByPath(String path) {
		return studyRepository.findStudyWithTagsByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyWithZonesByPath(String path) {
		return studyRepository.findStudyWithZonesByPath(path).orElseThrow(()->new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다."));
	}
	
	public Study getStudyToUpdate(String path, Account account) {
		Study study = this.getStudyByPath(path);
		
		checkIfManager(account, study);
		return study;
	}
	
	public Study getOnlyStudyToUpdate(String path, Account account) {
		Study study = this.getOnlyStudyByPath(path);
		checkIfManager(account, study);
		
		return study;
	}
	
	public Study getStudyWithTagsToUpdate(String path, Account account) {
		Study study = this.getStudyWithTagsByPath(path);
		checkIfManager(account, study);
		
		return study;
	}
	
	public Study getStudyWithZonesToUpdate(String path, Account account) {
		Study study = this.getStudyWithZonesByPath(path);
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
}
