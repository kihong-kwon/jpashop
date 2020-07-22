package com.kkhstudy.querydsl.controller;

import com.kkhstudy.querydsl.dto.StudentSearchCondition;
import com.kkhstudy.querydsl.dto.StudentTeamDto;
import com.kkhstudy.querydsl.repository.StudentJpaDataRepository;
import com.kkhstudy.querydsl.repository.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudentController {
    private final StudentJpaRepository studentJpaRepository;
    private final StudentJpaDataRepository studentJpaDataRepository;

    @GetMapping("/v1/students")
    public List<StudentTeamDto> searchStudentV1(StudentSearchCondition condition) {
        return studentJpaRepository.search(condition);
    }

    @GetMapping("/v2/students")
    public Page<StudentTeamDto> searchStudentV2(StudentSearchCondition condition, Pageable pageable) {
        return studentJpaDataRepository.searchPageSimple(condition, pageable);
    }

    @GetMapping("/v3/students")
    public Page<StudentTeamDto> searchStudentV3(StudentSearchCondition condition, Pageable pageable) {
        return studentJpaDataRepository.searchPageComplex(condition, pageable);
    }
}
