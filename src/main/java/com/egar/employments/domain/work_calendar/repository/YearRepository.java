package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.EmploymentsCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YearRepository extends JpaRepository<EmploymentsCalendar, Integer> {
}
