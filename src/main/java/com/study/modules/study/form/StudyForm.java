package com.study.modules.study.form;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.study.modules.study.Study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StudyForm {
	
	@NotBlank
	@Length(min = 2, max = 20)
	private String path;
	
	@NotBlank 
	@Length(max = 50)
	private String title;
	
	@NotBlank
	@Length(max = 100)
	private String shortDescription;
	
	@NotBlank
	private String fullDescription;
	
	public StudyForm(Study study) {
		this.setPath(study.getPath());
		this.setTitle(study.getTitle());
		this.setShortDescription(study.getShortDescription());
		this.setFullDescription(study.getFullDescription());
	}
	
}
