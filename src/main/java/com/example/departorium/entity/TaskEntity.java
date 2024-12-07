package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_main")
@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "task_title", columnDefinition = "varchar(32)", nullable = false)
    private String title;
    @Column(name = "task_description", columnDefinition = "varchar(1000)", nullable = false)
    private String description;
    @Column(name = "task_start", columnDefinition = "datetime", nullable = false)
    private LocalDateTime start;
    @Column(name = "task_end", columnDefinition = "datetime", nullable = false)
    private LocalDateTime end;
    @Column(name = "task_create", columnDefinition = "datetime", nullable = false)
    private LocalDateTime create;
    @Column(name = "task_update", columnDefinition = "datetime", nullable = false)
    private LocalDateTime update;
    @Column(name = "task_submit", columnDefinition = "datetime")
    private LocalDateTime submit;
    @Column(name = "task_approve", columnDefinition = "datetime")
    private LocalDateTime approve;
    @Column(name = "task_approval", columnDefinition = "varchar(255)")
    private String approval;
    @Column(name = "task_work", columnDefinition = "varchar(255)")
    private String work;
    @Column(name = "task_file", columnDefinition = "varchar(255)")
    private String file;
    @Column(name = "task_related", columnDefinition = "varchar(255)")
    private String related;
    @ManyToOne
    @JoinColumn(name = "depart_id")
    private DepartEntity depart;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    public void patch(TaskEntity task) {
        if (task.title != null)
            this.title = task.title;
        if (description != null)
            this.description = task.description;
        if (task.start != null)
            this.start = task.start;
        if (task.end != end)
            this.end = task.end;
        if (task.update != null)
            this.update = task.update;
        if (task.file != null)
            this.file = task.file;
        if (task.related != null)
            this.related = task.related;
    }
}
