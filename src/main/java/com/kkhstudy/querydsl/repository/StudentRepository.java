package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("select s from Student s where s.username = :username and s.age = :age")
    List<Student> findStudent(@Param("username") String username, @Param("age") int age);

    @Query("select s.username from Student s")
    List<String> findUsernameList();

    @Query("select new com.kkhstudy.querydsl.dto.StudentDto(s.id, s.username, t.name) from Student s join s.team t")
    List<String> findStudentDto();

    @Query("select s from Student s where s.username in :names")
    List<Student> findByNames(@Param("names") Collection<String> names);

    @Query(value = "select s from Student s", countQuery = "select count(s.username) from Student s")
    Page<Student> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // 업데이트가 끝나고 영속성 컨텍스트를 자동으로 비워준다.
    @Query("update Student s set s.age = s.age + 1 where s.age >= :age")
    int bulkAgePlus(@Param("age")int age);

    // @EntityGraph로 패치조인 하는 방법
    // @EntityGraph를 사용한 패치조인은 Left join 만 가능하다.
    // inner, outer 조인이 하고 싶은 경우 JPQL을 사용할것.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Student> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select s from Student s")
    List<Student> findMemberEntityGraph();

    //@EntityGraph(attributePaths = {"team"})
    @EntityGraph("Student.all")
    List<Student> findEntityGraphByUsername(@Param("username")String username);
}
