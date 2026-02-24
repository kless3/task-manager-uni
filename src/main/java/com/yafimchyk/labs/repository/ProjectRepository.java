package com.yafimchyk.labs.repository;

import com.yafimchyk.labs.model.Project;
import com.yafimchyk.labs.model.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByTitle(String title);

    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT DISTINCT p FROM Project p "
            + "LEFT JOIN p.tasks t "
            + "LEFT JOIN t.labels l "
            + "WHERE p.status = :status "
            + "AND l.title IN :labelTitles "
            + "AND p.deadline > :currentDate")
    List<Project> findProjectsByStatusAndLabels(
            @Param("status") ProjectStatus status,
            @Param("labelTitles") Set<String> labelTitles,
            @Param("currentDate") LocalDateTime currentDate);

    @Query(value = "SELECT DISTINCT p.* FROM projects p "
            + "LEFT JOIN tasks t ON p.id = t.project_id "
            + "LEFT JOIN task_labels tl ON t.id = tl.task_id "
            + "LEFT JOIN labels l ON tl.label_id = l.id "
            + "WHERE p.status = :status "
            + "AND l.title IN (:labelTitles) "
            + "AND p.deadline > :currentDate",
            nativeQuery = true)
    List<Project> findProjectsByStatusAndLabelsNative(
            @Param("status") String status,
            @Param("labelTitles") List<String> labelTitles,
            @Param("currentDate") LocalDateTime currentDate);
}