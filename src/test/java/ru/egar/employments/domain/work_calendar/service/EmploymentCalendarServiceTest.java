package ru.egar.employments.domain.work_calendar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ResourceUtils;
import ru.egar.employments.AbstractSpringBootTest;
import ru.egar.employments.domain.vacations.service.VacationService;
import ru.egar.employments.domain.work_calendar.entity.Employment;
import ru.egar.employments.domain.work_calendar.entity.Project;
import ru.egar.employments.domain.work_calendar.entity.WeekendAndShortDays;
import ru.egar.employments.domain.work_calendar.repository.EmploymentDayRepository;
import ru.egar.employments.domain.work_calendar.repository.ProjectRepository;
import ru.egar.employments.domain.work_calendar.repository.WeekendAndShortDayRepository;
import ru.egar.employments.model.EmploymentCalendarDto;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

class EmploymentCalendarServiceTest extends AbstractSpringBootTest {

    @Autowired
    EmploymentCalendarService employmentCalendarService;

    ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @MockBean
    private WeekendAndShortDayRepository weekendAndShortDayRepository;

    @MockBean
    private EmploymentDayRepository employmentDayRepository;

    @MockBean
    private VacationService vacationService;

    @MockBean
    private ProjectRepository projectRepository;

    @Test
    void getEmploymentCalendar() throws IOException {
        LocalDate firstDayOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        String egarId = "username";
        String profileListId = "180311895";
        String projectName = "НРД";
        List<WeekendAndShortDays> weekendAndShortDays = objectMapper
                .readValue(ResourceUtils.getFile(ResourceUtils
                        .CLASSPATH_URL_PREFIX.concat("employment/calendar/weekend_and_short.json")), new TypeReference<>() {});
        Set<LocalDate> vacationDaysSet = objectMapper
                .readValue(ResourceUtils.getFile(ResourceUtils
                        .CLASSPATH_URL_PREFIX.concat("employment/calendar/vacation_dates.json")), new TypeReference<>() {});
        List<Employment> employmentDays = objectMapper
                .readValue(ResourceUtils.getFile(ResourceUtils
                        .CLASSPATH_URL_PREFIX.concat("employment/calendar/list_employments.json")), new TypeReference<>() {});
        ObjectMapper mapper = new ObjectMapper();
        EmploymentCalendarDto calendarDto = mapper
                .readValue(ResourceUtils.getFile(ResourceUtils
                        .CLASSPATH_URL_PREFIX.concat("employment/calendar/employment_calendar.json")), new TypeReference<>() {});
        given(vacationService.getVacationDates(egarId, profileListId)).willReturn(vacationDaysSet);
        given(weekendAndShortDayRepository.findWeekendAndShortDays(firstDayOfYear, lastDayOfYear)).willReturn(weekendAndShortDays);
        given(projectRepository.findByProjectName(projectName)).willReturn(new Project(1L, "НРД"));
        given(employmentDayRepository.findEmployment(any(), anyString(), any(), any())).willReturn(employmentDays);
        assertThat(employmentCalendarService.getEmploymentCalendar(projectName, "1661994000000", egarId, profileListId))
                .isEqualTo(calendarDto);
    }
}