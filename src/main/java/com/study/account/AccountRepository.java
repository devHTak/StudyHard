package com.study.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long>{

	public boolean existsByEmail(String email);
	
	public boolean existsByNickname(String nickname);
	
	public Optional<Account> findByNickname(String nickname);
	
	public Optional<Account> findByEmail(String email);
}
