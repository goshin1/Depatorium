package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_token")
@Entity
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "token_refresh", nullable = false)
    private String refresh;
    @Column(name = "token_expiration", nullable = false)
    private LocalDateTime expiration;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
