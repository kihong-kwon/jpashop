package com.kkhstudy.jpashop.service;

import com.kkhstudy.jpashop.domain.Member;
import com.kkhstudy.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StudentServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @DisplayName("회원가입")
    @Test
    public void assign() throws Exception {
        Member member = new Member();
        member.setName("test");

        Long savedId = memberService.join(member);

        assertEquals(member, memberRepository.findOne(savedId));
        em.flush();
    }

    @DisplayName("중복 회원 예외")
    @Test
    public void duplicate_member() throws Exception {
        Member member1 = new Member();
        member1.setName("test");

        Member member2 = new Member();
        member2.setName("test");

        memberService.join(member1);
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });

        em.flush();
    }

}