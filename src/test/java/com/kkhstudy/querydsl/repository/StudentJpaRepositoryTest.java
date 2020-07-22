package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.domain.Team;
import com.kkhstudy.querydsl.dto.StudentSearchCondition;
import com.kkhstudy.querydsl.dto.StudentTeamDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentJpaRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    StudentJpaRepository studentJpaRepository;

    @Test
    public void searchTest() throws Exception {
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

        StudentSearchCondition condition = new StudentSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<StudentTeamDto> result = studentJpaRepository.searchByBuilder(condition);
        assertThat(result).extracting("username").containsExactly("member4");
    }
}