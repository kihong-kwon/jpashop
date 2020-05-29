package com.kkhstudy.querydsl;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.domain.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.kkhstudy.querydsl.domain.QStudent.student;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Student student1 = new Student("member1", 10, teamA);
        Student student2 = new Student("member2", 20, teamA);
        Student student3 = new Student("member3", 30, teamB);
        Student student4 = new Student("member4", 40, teamB);
        em.persist(student1);
        em.persist(student2);
        em.persist(student3);
        em.persist(student4);
    }

    @Test
    public void startQueyrdsl() {
        Student findStudent = queryFactory
                .select(student)
                .from(student)
                .where(student.username.eq("member1"))
                .fetchOne();
        assertThat(findStudent.getUsername()).isEqualTo("member1");
    }
}