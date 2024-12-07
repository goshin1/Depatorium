package com.example.departorium.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_member")
@Entity
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id", columnDefinition = "bigint", nullable = false)
    private Long id;
    @Column(name = "member_role", columnDefinition = "varchar(10)", nullable = false)
    private String role;
    @Column(name = "member_status", columnDefinition = "varchar(8)", nullable = false)
    private String status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "depart_id")
    private DepartEntity depart;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    public void patch(MemberEntity member) {
        if (member.role != null)
            this.role = member.role;
        if (member.status != null)
            this.status = member.status;
        if (member.depart != null)
            this.depart = member.depart;
    }
}
