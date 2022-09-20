package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WorkDayRepository extends JpaRepository<WeekendAndShortDays, Long> {
    List<WeekendAndShortDays> findWeekendAndShortDaysByDateBetween(LocalDate startDate, LocalDate endDate);
}
