package ru.egar.employments.integration_tests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HoursDto
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoursDto {

   @JsonProperty("work_hours")
   private Integer workHours;

   @JsonProperty("registered_hours")
   private Double registeredHours;
}

