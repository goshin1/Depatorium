package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_question")
@Entity
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "question_content", columnDefinition = "varchar(255)", nullable = false)
    private String content;
    @Column(name = "question_create", columnDefinition = "datetime", nullable = false)
    private LocalDateTime create;
    @Column(name = "question_update", columnDefinition = "datetime", nullable = false)
    private LocalDateTime update;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    public void patch(QuestionEntity question) {
        if (question.content != null)
            this.content = question.content;
        if (question.update != null)
            this.update = question.update;
    }
}
