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
	
	public Tag findByTitle(String title) {
		return tagRepository.findByTitle(title).orElseThrow(
				()-> new IllegalArgumentException(title +"에 해당하는 Tag가 존재하지 않습니다."));
	}

}
