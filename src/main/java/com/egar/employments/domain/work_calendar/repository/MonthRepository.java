package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthRepository extends JpaRepository<WorkCalendar, Integer> {
}
