package com.study.modules.tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long>{
	
	public Optional<Tag> findByTitle(String title);

}
