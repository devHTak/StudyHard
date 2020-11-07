package com.study.modules.event.event;

import com.study.modules.event.Enrollment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class EnrollmentEvent {
	
	private Enrollment enrollment;
	
	private String message;

}
