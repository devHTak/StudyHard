package com.study.modules.event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.modules.study.Study;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>{

	@EntityGraph(attributePaths = {"enrollments"}, type = EntityGraphType.LOAD)
	List<Event> findWithEnrollmentsByStudyOrderByStartDateTime(Study study);
	
	@EntityGraph(attributePaths = {"enrollments"}, type = EntityGraphType.LOAD)
	Optional<Event> findById(Long id);
}
