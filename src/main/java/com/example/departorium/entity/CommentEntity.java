package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule_comment")
@Entity
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "comment_content", columnDefinition = "varchar(255)", nullable = false)
    private String content;
    @Column(name = "comment_create", columnDefinition = "datetime", nullable = false)
    private LocalDateTime create;
    @Column(name = "comment_update", columnDefinition = "datetime", nullable = false)
    private LocalDateTime update;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity schedule;

    public void patch(CommentEntity comment) {
        if (comment.content != null)
            this.content = comment.content;
        if (comment.content != null)
            this.update = comment.update;
    }
}
