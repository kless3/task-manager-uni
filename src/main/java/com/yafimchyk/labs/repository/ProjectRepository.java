package com.yafimchyk.labs.repository;

import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByTitle(String title);

    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT DISTINCT p FROM Project p "
            + "JOIN p.tasks t "
            + "JOIN t.labels l "
            + "WHERE p.status = :status "
            + "AND p.deadline BETWEEN :startDate AND :endDate "
            + "AND l.title = :labelTitle")
    List<Project> findProjectsByStatusDeadlineAndLabelJpql(
            @Param("status") ProjectStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("labelTitle") String labelTitle);

    @Query(value = """
            SELECT DISTINCT p.* FROM projects p
            INNER JOIN tasks t ON p.id = t.project_id
            INNER JOIN task_labels tl ON t.id = tl.task_id
            INNER JOIN labels l ON tl.label_id = l.id
            WHERE p.status = CAST(:status AS project_status)
            AND p.deadline BETWEEN :startDate AND :endDate
            AND l.title = :labelTitle
            ORDER BY p.deadline ASC
            """, nativeQuery = true)
    List<Project> findProjectsByStatusDeadlineAndLabelNative(
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("labelTitle") String labelTitle);
}