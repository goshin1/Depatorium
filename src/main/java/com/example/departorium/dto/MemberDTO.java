package com.example.departorium.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private Long id;
    private String role;
    private String status;
    private Long depart_id;
}
