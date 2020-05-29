package com.kkhstudy.jpashop.repository;

import com.kkhstudy.jpashop.domain.annf.Bukken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BukkenRepository {

    private final EntityManager em;

    public void save(Bukken bukken) {
        em.persist(bukken);
    }

    public Bukken findOne(Long id) {
        return em.find(Bukken.class, id);
    }

    public List<Bukken> findAll() {
        return em.createQuery("select m from Student m", Bukken.class).getResultList();
    }

    public List<Bukken> findByName(String name) {
        return em.createQuery("select m from Student m where m.name = :name", Bukken.class).setParameter("name", name).getResultList();
    }
}
