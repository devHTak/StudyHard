package com.study.modules.study;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {
	
	Page<Study> findByKeyword(String keyword, Pageable pageable);
	
	List<Study> findStudyByTagsAndZones(Set<Tag> tags, Set<Zone> zones);

}
