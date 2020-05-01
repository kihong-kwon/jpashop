package com.kkhstudy.jpashop.domain.annf;

import com.kkhstudy.jpashop.domain.OrderItem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class SiteBukken {
    @Id
    @GeneratedValue
    @Column(name = "site_bukken_id")
    private Long id;

    private int siteCd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bukken_id")
    private Bukken bukken;

}
