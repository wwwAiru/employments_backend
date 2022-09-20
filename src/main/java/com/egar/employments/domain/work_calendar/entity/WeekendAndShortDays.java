package com.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "weekend")
@Getter
@Setter
@NoArgsConstructor
public class WeekendAndShortDays {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date", columnDefinition = "timestamp")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16)")
    private DayType dayType;

    public WeekendAndShortDays(LocalDate date) {
        this.date = date;
    }

}
