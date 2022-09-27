package ru.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Сущность занятость. Занятость сотрудника на проектах включает egarId, дату, количество учтённых часов,
 * связанная сущность Project
 */
@Entity
@Table(name = "employments")
@Getter
@Setter
@NoArgsConstructor
public class Employment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "egar_id", columnDefinition = "varchar(64)")
    private String egarId;

    @Column(name = "date", columnDefinition = "timestamp")
    private LocalDate date;

    @Column(name = "registered_hours", columnDefinition = "numeric(4,2)")
    private Double registeredHours;

    public Employment(Project project, String egarId) {
        this.project = project;
        this.egarId = egarId;
    }
}
