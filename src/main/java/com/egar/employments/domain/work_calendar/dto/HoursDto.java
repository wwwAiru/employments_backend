package com.egar.employments.domain.work_calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HoursDto {

    @JsonProperty("work_hours")
    private Integer workHours;

    @JsonProperty("registered_hours")
    private Double registeredHours;

    public HoursDto(Integer workHours, Double registeredHours) {
        this.workHours = workHours;
        this.registeredHours = registeredHours;
    }
}
