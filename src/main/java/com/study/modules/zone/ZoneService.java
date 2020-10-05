package com.study.modules.zone;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ZoneService {
	
	private final ZoneRepository zoneRepository;
	
	@PostConstruct
	public void postConstruct() throws IOException {
		if(zoneRepository.count() == 0) {
			Resource resource = new ClassPathResource("Cities in South Korea.csv");
			List<Zone> zones = Files.readAllLines(resource.getFile().toPath(), StandardCharsets.UTF_8).stream()
									.map(line -> {
										String[] split = line.split(",");
										return Zone.builder().city(split[0]).localNameCity(split[1]).province(split[2]).build();
									}).collect(Collectors.toList());
			zoneRepository.saveAll(zones);
		}
	}
	
	public List<Zone> findAll() {
		return zoneRepository.findAll();
	}
	
	public Zone findByCity(ZoneForm zoneForm) {
		return zoneRepository.findByCity(zoneForm.getCity()).orElseGet(Zone :: new);
	}
}
