package com.kkhstudy.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // QueryDSL에서 DTO를 사용하기 위해서는 DTO에 기본 생성자가 필요하다.
public class StudentDto {
    private Long id;
    private String username;
    private String teamName;
    private Integer age;

    public StudentDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    @QueryProjection
    public StudentDto(String username, Integer age) {
        this.username = username;
        this.age = age;
    }

}
