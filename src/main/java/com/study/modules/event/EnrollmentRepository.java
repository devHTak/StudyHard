package com.study.modules.event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

	public Optional<Enrollment> findByAccountAndEvent(Account account, Event event);
	
	@EntityGraph(value = "Enrollment.withEventAndStudy", type = EntityGraphType.LOAD)
	public List<Enrollment> findByAccount(Account account);
}
