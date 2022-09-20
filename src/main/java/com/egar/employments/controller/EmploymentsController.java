package com.egar.employments.controller;


import com.egar.employments.domain.employments.dto.EmploymentsDto;
import com.egar.employments.domain.employments.service.EmploymentService;
import com.egar.employments.domain.work_calendar.dto.EmploymentCalendarDto;
import com.egar.employments.domain.work_calendar.service.EmploymentCalendarService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<EmploymentsDto>> getTaskById(@RequestParam("id") List<String> id) {
        List<EmploymentsDto> employments = employmentService.getEmployments(id);
        return new ResponseEntity<> (employments, HttpStatus.OK);
    }

    @GetMapping("/calendary/")
    public ResponseEntity<EmploymentCalendarDto> getEmploymentCalendar(@RequestParam("projectName") String projectName,
                                                                       @RequestParam("beginDate") String beginDate) {
        return new ResponseEntity<>(employmentCalendarService.getEmploymentCalendar(projectName, beginDate), HttpStatus.OK);
    }
}
