package com.study.modules.study.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PathForm {
	
	
	@NotBlank
	@Length(min = 2, max = 20)
	private String path;

}
