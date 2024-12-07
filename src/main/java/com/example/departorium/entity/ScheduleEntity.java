package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule_main")
@Entity
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "schedule_title", columnDefinition = "varchar(32)", nullable = false)
    private String title;
    @Column(name = "schedule_detail", columnDefinition = "varchar(255)", nullable = false)
    private String detail;
    @Column(name = "schedule_start", columnDefinition = "datetime", nullable = false)
    private LocalDateTime start;
    @Column(name = "schedule_end", columnDefinition = "datetime", nullable = false)
    private LocalDateTime end;
    @Column(name = "schedule_create", columnDefinition = "datetime", nullable = false)
    private LocalDateTime create;
    @Column(name = "schedule_update", columnDefinition = "datetime", nullable = false)
    private LocalDateTime update;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    public void patch(ScheduleEntity schedule) {
        if (schedule.title != null)
            this.title = schedule.title;
        if (schedule.detail != null)
            this.detail = schedule.detail;
        if (schedule.start != null)
            this.start = schedule.start;
        if (schedule.end != null)
            this.end = schedule.end;
        if (schedule.update != null)
            this.update = schedule.update;
    }
}
