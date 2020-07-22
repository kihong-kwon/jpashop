package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.dto.StudentSearchCondition;
import com.kkhstudy.querydsl.dto.StudentTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentRepositoryCustom {
    List<StudentTeamDto> search(StudentSearchCondition condition);
    Page<StudentTeamDto> searchPageSimple(StudentSearchCondition condition, Pageable pageable);
    Page<StudentTeamDto> searchPageComplex(StudentSearchCondition condition, Pageable pageable);
}
