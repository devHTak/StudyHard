package com.study.modules.zone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ZoneForm {
	
	private String fullCity;
	
	public String getCity() {
		return fullCity.indexOf("(") >= 0 ? fullCity.substring(0, fullCity.indexOf("(")) : fullCity;
	}
	
	public String getProvinceName() {
		return fullCity.indexOf("(") >= 0 ? fullCity.substring(fullCity.indexOf("/") + 1) : fullCity;
	}
	
	public String getLocalNameCity() {
		return fullCity.indexOf("(") >= 0 ? fullCity.substring(fullCity.indexOf("(") + 1, fullCity.indexOf(")")) : fullCity;
	}

}
