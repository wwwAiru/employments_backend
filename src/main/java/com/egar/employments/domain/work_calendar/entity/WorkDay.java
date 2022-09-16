package com.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_days")
@Getter
@Setter
@NoArgsConstructor
public class WorkDay {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private DayType dayType;

    public WorkDay(LocalDate date) {
        this.date = date;
    }

    public WorkDay(LocalDate date, DayType dayType) {
        this.date = date;
        this.dayType = dayType;
    }

}
