package com.yafimchyk.labs.repository;

import com.yafimchyk.labs.model.Task;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Task} entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  Optional<Task> findByTitle(String title);
}