package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project_chat")
@Entity
public class ChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "chat_content", columnDefinition = "varchar(1000)", nullable = false)
    private String content;
    @Column(name = "chat_log", columnDefinition = "datetime", nullable = false)
    private LocalDateTime log;
    @Column(name = "chat_type", columnDefinition = "varchar(255)")
    private String type;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;
    @ManyToOne
    @JoinColumn(name = "depart_id")
    private DepartEntity depart;
}
