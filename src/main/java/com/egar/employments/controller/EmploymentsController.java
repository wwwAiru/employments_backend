package com.egar.employments.controller;


import com.egar.employments.domain.employments.dto.EmploymentDto;
import com.egar.employments.domain.employments.service.EmploymentService;
import com.egar.employments.domain.work_calendar.dto.EmploymentCalendarDto;
import com.egar.employments.domain.work_calendar.service.EmploymentCalendarService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employments")
@RequiredArgsConstructor
public class EmploymentsController {

    private final EmploymentService employmentService;

    private final EmploymentCalendarService employmentCalendarService;

    @GetMapping("/")
    public ResponseEntity<List<EmploymentDto>> getTaskById(@RequestParam("id") List<String> id) {
        List<EmploymentDto> employments = employmentService.getEmployments(id);
        return new ResponseEntity<> (employments, HttpStatus.OK);
    }

    @GetMapping("/calendar")
    @SneakyThrows
    public ResponseEntity<EmploymentCalendarDto> getEmploymentCalendar(
            @RequestParam("project_name") String projectName,
            @RequestParam("begin_date") String beginDate,
            @RequestParam(value = "egar_id") String egarId,
            @RequestParam(value = "profile_list_id") String profileListId
    ) {
        EmploymentCalendarDto employmentCalendar = employmentCalendarService
                .getEmploymentCalendar(projectName, beginDate, egarId, profileListId);
        return new ResponseEntity<>(employmentCalendar, HttpStatus.OK);
    }
}
