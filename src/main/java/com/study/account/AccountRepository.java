package com.study.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long>{

	public boolean existsByEmail(String email);
	public boolean existsByNickname(String nickname);
}
