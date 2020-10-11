package com.study.modules.study.form;

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
public class DescriptionForm {
	
	@NotBlank
	@Length(max = 100)
	private String shortDescription;
	
	@NotBlank
	private String fullDescription;
	
	public DescriptionForm(Study study) {
		this.setShortDescription(study.getShortDescription());
		this.setFullDescription(study.getFullDescription());
	}
}
