package ru.egar.employments.domain.work_calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.egar.employments.domain.work_calendar.entity.Employment;
import ru.egar.employments.domain.work_calendar.entity.Project;

import java.time.LocalDate;
import java.util.List;
public interface EmploymentDayRepository extends JpaRepository<Employment, Long> {

    @Query("select e from Employment e where e.project = ?1 and e.egarId = ?2 and e.date between ?3 and ?4")
    List<Employment> findEmployment(Project project, String egarId, LocalDate startDate, LocalDate endDate);
}
