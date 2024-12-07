package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_depart")
@Entity
public class DepartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depart_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "depart_subtitle", columnDefinition = "varchar(32)", unique = true, nullable = false)
    private String subtitle;
    @Column(name = "depart_token", columnDefinition = "varchar(36)", nullable = false)
    private String token;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    public void patch(DepartEntity depart) {
        if (depart.subtitle != null)
            this.subtitle = depart.subtitle;
    }
}
