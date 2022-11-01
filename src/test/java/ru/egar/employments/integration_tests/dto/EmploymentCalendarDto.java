package ru.egar.employments.integration_tests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * EmploymentCalendarDto
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmploymentCalendarDto {

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("work_calendar")
    private Map<String, HoursDto> workCalendar = null;
}

