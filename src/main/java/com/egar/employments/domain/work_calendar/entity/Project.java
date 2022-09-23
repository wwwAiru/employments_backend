package com.egar.employments.domain.work_calendar.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    private List<Employment> employments;

    public Project(String projectName) {
        this.projectName = projectName;
    }

}
