package ru.egar.employments.domain.work_calendar.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import ru.egar.employments.AbstractSpringBootTest;
import ru.egar.employments.domain.vacations.service.VacationService;
import ru.egar.employments.domain.work_calendar.entity.Employment;
import ru.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
import ru.egar.employments.domain.work_calendar.repository.EmploymentDayRepository;
import ru.egar.employments.domain.work_calendar.repository.WeekendAndShortDayRepository;
import ru.egar.employments.model.EmploymentCalendarDto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@Sql(scripts = {"classpath:data/test-data.sql"})
@Sql(scripts = "classpath:data/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EmploymentCalendarServiceTest extends AbstractSpringBootTest {

    @Autowired
    private EmploymentCalendarService employmentCalendarService;

    @Autowired
    private WeekendAndShortDayRepository weekendAndShortDayRepository;

    @Autowired
    private EmploymentDayRepository employmentDayRepository;

    @MockBean
    private VacationService vacationService;

    @Test
    public void getAllWeekendsNotEmpty() {
        List<WeekendAndShortDays> all = weekendAndShortDayRepository.findAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    public void getAllEmploymentsNotEmpty() {
        List<Employment> all = employmentDayRepository.findAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    void getEmploymentCalendar()  {
        String egarId = "username";
        String profileListId = "180311895";
        String projectName = "НРД";
        LocalDate vacationStart = LocalDate.parse("2022-11-02");
        LocalDate vacationEnd = LocalDate.parse("2022-11-15");
        Set<LocalDate> vacations = new HashSet<>();
        while (!vacationStart.isAfter(vacationEnd)) {
            vacations.add(vacationStart);
            vacationStart = vacationStart.plusDays(1);
        }
        given(vacationService.getVacationDates(egarId, profileListId)).willReturn(vacations);
        EmploymentCalendarDto employmentCalendar = employmentCalendarService.getEmploymentCalendar(projectName, "1640912400000", egarId, profileListId);
        assertThat(employmentCalendar.getProjectName()).isEqualTo(projectName);
        assertThat(employmentCalendar.getWorkCalendar()).isNotNull();
    }
}