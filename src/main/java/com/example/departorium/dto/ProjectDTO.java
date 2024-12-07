package com.example.departorium.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProjectDTO {
    private Long id;
    private String title;
    private String comment;
    private String invitation;
    private String status;
}
