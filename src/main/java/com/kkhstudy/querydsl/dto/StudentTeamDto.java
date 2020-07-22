package com.kkhstudy.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class StudentTeamDto {
    private Long studentId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public StudentTeamDto(Long studentId, String username, int age, Long teamId, String teamName) {
        this.studentId = studentId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
