package com.yafimchyk.labs.repository;

import com.yafimchyk.labs.model.Task;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTitle(String title);

    List<Task> findByProjectId(Long projectId);

    @Query("select t from Task t")
    @EntityGraph(attributePaths = {"comments", "labels"})
    List<Task> findAllWithGraph();



}