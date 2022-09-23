package com.egar.employments.domain.employments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmploymentDto {

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("begin_date")
    private String startEmploymentDate;

    public EmploymentDto(String name, String date) {
        this.projectName = name;
        this.startEmploymentDate = date;
    }
}
