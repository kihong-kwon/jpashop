package com.kkhstudy.jpashop.domain;

import com.kkhstudy.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item_details")
@Getter
@Setter
public class OrderItemDetail {
    @Id
    @GeneratedValue
    @Column(name = "order_item_detail_id")
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;
}
