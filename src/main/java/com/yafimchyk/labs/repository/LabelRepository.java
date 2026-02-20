package com.yafimchyk.labs.repository;

import com.yafimchyk.labs.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    boolean existsByTitle(String title);
}
