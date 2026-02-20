package com.yafimchyk.labs.repository;

import com.yafimchyk.labs.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByProjectId(Long projectId);

    boolean existsByTitle(String title);
}
