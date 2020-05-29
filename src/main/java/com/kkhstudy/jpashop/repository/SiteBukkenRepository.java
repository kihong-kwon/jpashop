package com.kkhstudy.jpashop.repository;

import com.kkhstudy.jpashop.domain.annf.SiteBukken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SiteBukkenRepository {

    private final EntityManager em;

    public void save(SiteBukken siteBukken) {
        em.persist(siteBukken);
    }

    public SiteBukken findOne(Long id) {
        return em.find(SiteBukken.class, id);
    }

    public List<SiteBukken> findAll() {
        return em.createQuery("select m from Student m", SiteBukken.class).getResultList();
    }

    public List<SiteBukken> findByName(String name) {
        return em.createQuery("select m from Student m where m.name = :name", SiteBukken.class).setParameter("name", name).getResultList();
    }
}
