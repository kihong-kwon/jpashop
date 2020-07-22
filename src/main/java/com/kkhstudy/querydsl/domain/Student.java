package com.kkhstudy.querydsl.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedEntityGraph(name = "Student.all", attributeNodes = @NamedAttributeNode("team"))
public class Student {
    @Id
    @GeneratedValue
    @Column(name = "student_id")
    private Long id;
    private String username;
    private int age;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "student")
    List<Photo> photos = new ArrayList<>();

    public Student(String username) {
        this(username, 0);
    }

    public Student(String username, int age) {
        this(username, age, null);
    }

    public Student(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getStudents().add(this);
    }
}
