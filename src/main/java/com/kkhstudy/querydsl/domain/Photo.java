package com.kkhstudy.querydsl.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "filePath"})
public class Photo {
    @Id
    @GeneratedValue
    @Column(name = "photo_id")
    private Long id;
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    public Photo(String filePath, Student student) {
        this.filePath = filePath;
        this.student = student;
    }
}
