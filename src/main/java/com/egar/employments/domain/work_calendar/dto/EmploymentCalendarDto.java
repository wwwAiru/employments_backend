package com.egar.employments.domain.work_calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class EmploymentCalendarDto {

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("work_calendar")
    private Map<Integer, HoursDto> workCalendar;

    public EmploymentCalendarDto(String projectName, Map<Integer, HoursDto> workCalendar) {
        this.projectName = projectName;
        this.workCalendar = workCalendar;
    }
}
