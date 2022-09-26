package ru.egar.employments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.egar.employments.domain.employments.service.EmploymentService;
import ru.egar.employments.domain.work_calendar.service.EmploymentCalendarService;
import ru.egar.employments.model.EmploymentCalendarDto;
import ru.egar.employments.model.EmploymentDto;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentsControllerService {

    private final EmploymentCalendarService employmentCalendarService;

    private final EmploymentService employmentService;

    @Override
    public EmploymentCalendarDto getEmploymentCalendar(String projectName, String beginDate, String egarId, String profileListId) {
        return employmentCalendarService.getEmploymentCalendar(projectName, beginDate, egarId, profileListId);
    }

    @Override
    public List<EmploymentDto> getTaskById(List<String> id) {
        return employmentService.getEmployments(id);
    }
}
