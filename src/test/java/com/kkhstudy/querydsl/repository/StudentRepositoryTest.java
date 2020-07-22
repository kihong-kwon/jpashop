package com.kkhstudy.querydsl.repository;

import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.dto.StudentDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    public void test() {
        //given
        studentRepository.save(new Student("student1", 10));
        studentRepository.save(new Student("student2", 10));
        studentRepository.save(new Student("student3", 10));
        studentRepository.save(new Student("student4", 10));
        studentRepository.save(new Student("student5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //When
        Page<Student> page = studentRepository.findByAge(age, pageRequest);
        Page<StudentDto> toMap = page.map(m -> new StudentDto(m.getId(), m.getUsername(), null));

        //Then
        List<Student> content = page.getContent();
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void test2() {
        //given
        studentRepository.save(new Student("student1", 10));
        studentRepository.save(new Student("student2", 10));
        studentRepository.save(new Student("student3", 10));
        studentRepository.save(new Student("student4", 10));
        studentRepository.save(new Student("student5", 10));

    }



}
