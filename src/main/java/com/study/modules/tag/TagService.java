package com.study.modules.tag;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
	
	private final TagRepository tagRepository;
	
	public List<Tag> findAll() {
		return tagRepository.findAll();
	}
	
	public Tag save(TagForm tagForm) {
		return tagRepository.findByTitle(tagForm.getTitle()).orElseGet(
				() -> tagRepository.save(Tag.builder().title(tagForm.getTitle()).build()));
	}

	public Tag remove(TagForm tagForm) {
		return tagRepository.findByTitle(tagForm.getTitle()).orElseGet(Tag :: new);
	}
	
	

}
