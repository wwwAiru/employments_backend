package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.EmploymentDay;
import com.egar.employments.domain.work_calendar.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmploymentDayRepository extends JpaRepository<EmploymentDay, Long> {
}
