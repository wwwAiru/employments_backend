package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByProjectName(String name);
}
