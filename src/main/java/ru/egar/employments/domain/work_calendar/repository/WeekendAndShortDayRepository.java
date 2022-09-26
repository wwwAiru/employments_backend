package ru.egar.employments.domain.work_calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;

import java.time.LocalDate;
import java.util.List;

public interface WeekendAndShortDayRepository extends JpaRepository<WeekendAndShortDays, Long> {
    @Query("select w from WeekendAndShortDays w where w.date between ?1 and ?2")
    List<WeekendAndShortDays> findWeekendAndShortDays(LocalDate startDate, LocalDate endDate);
}
