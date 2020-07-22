package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
