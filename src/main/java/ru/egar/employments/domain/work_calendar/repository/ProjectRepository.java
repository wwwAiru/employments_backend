package ru.egar.employments.domain.work_calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.egar.employments.domain.work_calendar.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByProjectName(String name);
}
