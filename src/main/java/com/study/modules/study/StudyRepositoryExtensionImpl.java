package com.study.modules.study;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.study.modules.account.QAccount;
import com.study.modules.tag.QTag;
import com.study.modules.tag.Tag;
import com.study.modules.zone.QZone;
import com.study.modules.zone.Zone;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {
	
	public StudyRepositoryExtensionImpl() {
		super(Study.class);
	}

	@Override
	public Page<Study> findByKeyword(String keyword, Pageable pageable) {
		// TODO Auto-generated method stub
		
		QStudy study = QStudy.study;
		JPQLQuery<Study> query = from(study).where(study.published.isTrue()
													.and(study.title.containsIgnoreCase(keyword))
													.or(study.tags.any().title.containsIgnoreCase(keyword))
													.or(study.zones.any().localNameCity.containsIgnoreCase(keyword)))
													.leftJoin(study.tags, QTag.tag ).fetchJoin()
													.leftJoin(study.zones, QZone.zone).fetchJoin()
													.leftJoin(study.members, QAccount.account).fetchJoin()
													.distinct();
		
		JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
		QueryResults<Study> fetchResults = pageableQuery.fetchResults();
		return new PageImpl<Study>(fetchResults.getResults(), pageable, fetchResults.getTotal());
	}

	@Override
	public List<Study> findStudyByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
		// TODO Auto-generated method stub
		QStudy study = QStudy.study;
		JPQLQuery<Study> query = from(study).where(study.tags.any().in(tags)
				.and(study.zones.any().in(zones))
				.and(study.published.isTrue())
				.and(study.closed.isFalse()))
				.leftJoin(study.tags, QTag.tag).fetchJoin()
				.leftJoin(study.zones, QZone.zone).fetchJoin()
				.leftJoin(study.members, QAccount.account).fetchJoin()
				.orderBy(study.publishedDateTime.desc())
				.distinct()
				.limit(9);
		
		return query.fetch();
	}
}
