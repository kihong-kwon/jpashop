package com.kkhstudy.jpashop.domain.annf;

import com.kkhstudy.jpashop.domain.OrderItem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Bukken {

    @Id
    @GeneratedValue
    @Column(name = "bukken_id")
    private Long id;

    private int bukkenKbn;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "bukken", cascade = CascadeType.ALL)
    private List<SiteBukken> siteBukkens = new ArrayList<>();

    public void addSiteBukken(SiteBukken siteBukken) {
        siteBukkens.add(siteBukken);
        siteBukken.setBukken(this);
    }
}
