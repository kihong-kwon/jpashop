package com.kkhstudy.jpashop.repository;

import com.kkhstudy.jpashop.domain.annf.Bukken;
import com.kkhstudy.jpashop.domain.annf.SiteBukken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@SpringBootTest
@AutoConfigureDataJpa
class SiteBukkenRepositoryTest {
    @Autowired
    private BukkenRepository bukkenRepository;

    @Test
    @Rollback(false)
    public void test() {
        Bukken bukken = new Bukken();
        bukken.setBukkenKbn(1);
        bukken.setCreateDate(LocalDateTime.now());
        bukkenRepository.save(bukken);

        SiteBukken siteBukken1 = new SiteBukken();
        siteBukken1.setSiteCd(1);
        siteBukken1.setBukken(bukken);
        SiteBukken siteBukken2 = new SiteBukken();
        siteBukken2.setSiteCd(2);
        siteBukken2.setBukken(bukken);
        SiteBukken siteBukken3 = new SiteBukken();
        siteBukken3.setSiteCd(3);
        siteBukken3.setBukken(bukken);

        bukken.getSiteBukkens().add(siteBukken1);
        bukken.getSiteBukkens().add(siteBukken2);
        bukken.getSiteBukkens().add(siteBukken3);

    }

}