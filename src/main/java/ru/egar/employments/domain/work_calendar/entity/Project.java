package ru.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * сущность проекты, содержит названия проектов, связанная сущность Employment
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "project_name", columnDefinition = "varchar(255)")
    private String projectName;

    @OneToMany(mappedBy = "project")
    private List<Employment> employments;

    public Project(String projectName) {
        this.projectName = projectName;
    }

    public Project(Long id, String projectName) {
        this.id = id;
        this.projectName = projectName;
    }
}
