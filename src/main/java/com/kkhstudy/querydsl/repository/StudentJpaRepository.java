package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.dto.QStudentTeamDto;
import com.kkhstudy.querydsl.dto.StudentSearchCondition;
import com.kkhstudy.querydsl.dto.StudentTeamDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.kkhstudy.querydsl.domain.QStudent.student;
import static com.kkhstudy.querydsl.domain.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class StudentJpaRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public StudentJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public void save(Student student) {
        em.persist(student);
    }

    public List<StudentTeamDto> searchByBuilder(StudentSearchCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        if (hasText(condition.getUsername())) {
            builder.and(student.username.eq(condition.getUsername()));
        }
        if (hasText(condition.getTeamName())) {
            builder.and(team.name.eq(condition.getTeamName()));
        }
        if (condition.getAgeGoe() != null) {
            builder.and(student.age.goe(condition.getAgeGoe()));
        }
        if (condition.getAgeLoe() != null) {
            builder.and(student.age.loe(condition.getAgeLoe()));
        }
        return queryFactory
                .select(new QStudentTeamDto(
                        student.id.as("memberId"),
                        student.username,
                        student.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(student)
                .leftJoin(student.team, team)
                .where(builder)
                .fetch();
    }

    public List<StudentTeamDto> search(StudentSearchCondition condition) {
        return queryFactory
                .select(new QStudentTeamDto(
                        student.id.as("memberId"),
                        student.username,
                        student.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(student)
                .leftJoin(student.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    private BooleanExpression usernameEq(String username) {
        return hasText(username) ? student.username.eq(username) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? student.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? student.age.loe(ageLoe) : null;
    }
}
