package com.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employment_days")
@Getter
@Setter
@NoArgsConstructor
public class EmploymentDay {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "date", columnDefinition = "timestamp")
    private LocalDate date;

    @Column(name = "registered_hours", columnDefinition = "numeric(2,2)")
    private Double registeredHours;
}
