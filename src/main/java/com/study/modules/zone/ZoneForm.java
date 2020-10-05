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
		return this.fullCity.substring(0, this.fullCity.indexOf("/")).trim();
	}
	
	public String getLocalNameCity() {
		return this.fullCity.substring(this.fullCity.indexOf("/") + 1, this.fullCity.indexOf("(")).trim();
	}
	
	public String getProvince() {
		return this.fullCity.substring(this.fullCity.indexOf("(") + 1, this.fullCity.indexOf(")")).trim();
	}

}
