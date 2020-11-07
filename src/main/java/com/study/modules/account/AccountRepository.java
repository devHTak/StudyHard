package com.study.modules.account;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.study.modules.tag.Tag;
import com.study.modules.zone.Zone;


public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account>{

	public boolean existsByEmail(String email);
	
	public boolean existsByNickname(String nickname);
	
	public Optional<Account> findByNickname(String nickname);
	
	public Optional<Account> findByEmail(String email);
	
	@EntityGraph(attributePaths = {"tags"}, type = EntityGraphType.LOAD)
	public Account findAccountWithTagsById(Long id);
	
	@EntityGraph(attributePaths = {"zones"}, type = EntityGraphType.LOAD)
	public Account findAccountWithZonesById(Long id);
	
	@EntityGraph(attributePaths = {"tags", "zones"}, type=EntityGraphType.LOAD)
	public Optional<Account> findAccountWithTagsAndZonesById(Long id);
	
	
}
