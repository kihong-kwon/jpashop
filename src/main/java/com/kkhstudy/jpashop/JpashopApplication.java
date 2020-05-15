package com.kkhstudy.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

    @Bean
    Hibernate5Module hibernate5Module() {
        // 초기화 된 프록시 객체만 노출, 프록시 객체가 참조될때 에러가 발생하지 않게 해줌.
        return new Hibernate5Module();
    }

}
