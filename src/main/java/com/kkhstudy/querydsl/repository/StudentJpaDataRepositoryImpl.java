package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.dto.QStudentTeamDto;
import com.kkhstudy.querydsl.dto.StudentSearchCondition;
import com.kkhstudy.querydsl.dto.StudentTeamDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.kkhstudy.querydsl.domain.QStudent.student;
import static com.kkhstudy.querydsl.domain.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

// 이 클래서는 스프링 데이터 인터페이스 이름과 맞춰줘야 한다. : XXXRepositoryImpl
public class StudentJpaDataRepositoryImpl implements StudentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public StudentJpaDataRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
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

    @Override
    public Page<StudentTeamDto> searchPageSimple(StudentSearchCondition condition, Pageable pageable) {
        QueryResults<StudentTeamDto> results = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults(); // 데이터 쿼리와 카운트 쿼리가 발생.

        List<StudentTeamDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<StudentTeamDto> searchPageComplex(StudentSearchCondition condition, Pageable pageable) {
        List<StudentTeamDto> content = queryFactory
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Student> countQuery = queryFactory
                .select(student)
                .from(student)
                .leftJoin(student.team, team)
                .where(
                        usernameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );
        // 첫 페이지나 마지막 페이지의 건수가 페이지 사이즈 보다 작을경우 카운트 쿼리를 생략해준다.
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
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
