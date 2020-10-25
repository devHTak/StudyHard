package com.study.modules.event;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.study.modules.account.Account;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

	public Optional<Enrollment> findByAccountAndEvent(Account account, Event event);
}
