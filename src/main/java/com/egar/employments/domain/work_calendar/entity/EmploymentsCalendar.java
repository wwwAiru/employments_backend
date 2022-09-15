package com.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "employments_calendar")
@Getter
@Setter
public class EmploymentsCalendar {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;

    @Column(name = "registered_hours")
    private Integer registeredHours;
}
