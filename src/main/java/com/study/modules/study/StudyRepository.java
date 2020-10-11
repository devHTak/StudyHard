package com.study.modules.study;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>{
	
	public boolean existsByPath(String path);
	
	@EntityGraph(attributePaths = {"managers", "members", "tags", "zones"}, type = EntityGraphType.LOAD)
	public Optional<Study> findByPath(String path);
	
	@EntityGraph(attributePaths = {"tags"}, type=EntityGraphType.LOAD)
	public Optional<Study> findStudyWithTagsByPath(String path);
	
	@EntityGraph(attributePaths = {"zones"}, type=EntityGraphType.LOAD)
	public Optional<Study> findStudyWithZonesByPath(String path);
	
	public Optional<Study> findOnlyStudyByPath(String path);
}
