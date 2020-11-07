package com.study.modules.study.event;

import com.study.modules.study.Study;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class StudyCreatedEvent {
	
	private Study study;
	private String message;
	
	
	public StudyCreatedEvent() {}
	
	public StudyCreatedEvent(Study study) {
		this.study = study;
		this.message = study.getTitle() +" 스터디가 생성되었습니다.";
	}
	

}
