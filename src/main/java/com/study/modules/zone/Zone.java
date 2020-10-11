package com.study.modules.zone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Zone {

	@Id @GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String city;
	
	@Column(nullable = false)
	private String localNameCity;
	
	@Column(nullable = true)
	private String province;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s(%s)/%s", city, localNameCity, province);
	}
	
	
}
