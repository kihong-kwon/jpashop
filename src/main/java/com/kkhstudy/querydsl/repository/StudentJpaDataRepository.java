package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentJpaDataRepository extends JpaRepository<Student, Long>, StudentRepositoryCustom {
    List<Student> findByUsername(String username);
}
