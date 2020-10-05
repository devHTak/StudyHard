package com.study.modules.zone;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long>{

	Optional<Zone> findByCity(String city);
}
