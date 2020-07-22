package com.kkhstudy.querydsl.controller;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.domain.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitStudent {
    private final InitStudentService initStudentService;

    @PostConstruct
    public void init() {
        initStudentService.init();
    }

    @Component
    static class InitStudentService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);

            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Student("member" + i, i, selectedTeam));
            }
        }
    }
}
