package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_main")
@Entity
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "project_title", columnDefinition = "varchar(32)", unique = true, nullable = false)
    private String title;
    @Column(name = "project_comment", columnDefinition = "varchar(2000)")
    private String comment;
    @Column(name = "project_invitation", columnDefinition = "varchar(32)", nullable = false)
    private String invitation;
    @Column(name = "project_status", columnDefinition = "varchar(8)", nullable = false)
    private String status;

    public void patch(ProjectEntity project) {
        if (project.title != null)
            this.title = project.title;
        if (project.comment != null)
            this.comment = project.comment;
        if (project.invitation != null)
            this.invitation = project.invitation;
        if (project.status != null)
            this.status = project.status;
    }
}
