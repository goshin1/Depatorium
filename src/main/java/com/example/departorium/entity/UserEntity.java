package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_main")
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "user_username", columnDefinition = "varchar(255)", unique = true, nullable = false)
    private String username;
    @Column(name = "user_password", columnDefinition = "varchar(255)")
    private String password;
    @Column(name = "user_nickname", columnDefinition = "varchar(32)", nullable = false)
    private String nickname;
    @Column(name = "user_email", columnDefinition = "varchar(320)", nullable = false)
    private String email;
    @Column(name = "user_birth", columnDefinition = "date")
    private LocalDate birth;
    @Column(name = "user_phone", columnDefinition = "varchar(13)")
    private String phone;
    @Column(name = "user_group", columnDefinition = "varchar(32)")
    private String group;
    @Column(name = "user_theme", columnDefinition = "varchar(5)")
    private String theme;
    @Column(name = "user_url", columnDefinition = "varchar(255)")
    private String url;
    @Column(name = "user_role", columnDefinition = "varchar(10)", nullable = false)
    private String role;
    @Column(name = "user_status", columnDefinition = "varchar(8)", nullable = false)
    private String status;

    public void patch(UserEntity profile) {
        if (profile.nickname != null)
            this.nickname = profile.nickname;
        if (profile.email != null)
            this.email = profile.email;
        if (profile.birth != null)
            this.birth = profile.birth;
        if (profile.phone != null)
            this.phone = profile.phone;
        if (profile.group != null)
            this.group = profile.group;
        if (profile.theme != null)
            this.theme = profile.theme;
        if (profile.url != null)
            this.url = profile.url;
        if (profile.role != null)
            this.role = profile.role;
        if (profile.status != null)
            this.status = profile.status;
    }
}
