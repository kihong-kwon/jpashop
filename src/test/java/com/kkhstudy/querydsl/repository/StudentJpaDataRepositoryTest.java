package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.domain.Team;
import com.kkhstudy.querydsl.dto.StudentSearchCondition;
import com.kkhstudy.querydsl.dto.StudentTeamDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StudentJpaDataRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    StudentJpaDataRepository studentJpaDataRepository;

    @Test
    public void basicTest() {
        Student student = new Student("member1", 10);
        studentJpaDataRepository.save(student);

        Student findStudent = studentJpaDataRepository.findById(student.getId()).get();
        assertThat(findStudent).isEqualTo(student);

        List<Student> result1 = studentJpaDataRepository.findAll();
        assertThat(result1).containsExactly(student);

        List<Student> result2 = studentJpaDataRepository.findByUsername("member1");
        assertThat(result2).containsExactly(student);
    }

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

        List<StudentTeamDto> result = studentJpaDataRepository.search(condition);
        assertThat(result).extracting("username").containsExactly("member4");
    }

    @Test
    public void searchPageSimple() throws Exception {
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
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<StudentTeamDto> result = studentJpaDataRepository.searchPageSimple(condition, pageRequest);
        assertThat(result.getSize()).isEqualTo(3);
        assertThat(result.getContent()).extracting("username").containsExactly("member1", "member2", "member3");
    }

}