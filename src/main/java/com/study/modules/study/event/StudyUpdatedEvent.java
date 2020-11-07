package com.study.modules.study.event;

import com.study.modules.study.Study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
public class StudyUpdatedEvent {
	
	private Study study;
	private String message;
	
	public StudyUpdatedEvent() {}
	
	public StudyUpdatedEvent(Study study, String message) {
		this.study = study;
		this.message = message;
	}

}
