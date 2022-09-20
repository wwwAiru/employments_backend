package com.egar.employments.domain.work_calendar.repository;

import com.egar.employments.domain.work_calendar.entity.EmploymentDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EmploymentDayRepository extends JpaRepository<EmploymentDay, Long> {

    @Query("select ed from EmploymentDay ed where lower(ed.projectName) like lower(concat('%', ?1,'%')) and ed.date between ?2 and ?3")
    List<EmploymentDay> findEmploymentDay(String projectName, LocalDate startDate, LocalDate endDate);

}
