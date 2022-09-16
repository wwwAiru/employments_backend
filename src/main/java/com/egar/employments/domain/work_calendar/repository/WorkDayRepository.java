package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
    List<WorkDay> findWorkDayByDateBetween(LocalDate startDate, LocalDate endDate);
}
