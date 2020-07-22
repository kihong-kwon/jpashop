package com.kkhstudy.querydsl;

import com.kkhstudy.querydsl.domain.Photo;
import com.kkhstudy.querydsl.domain.QStudent;
import com.kkhstudy.querydsl.domain.Student;
import com.kkhstudy.querydsl.domain.Team;
import com.kkhstudy.querydsl.dto.QStudentDto;
import com.kkhstudy.querydsl.dto.StudentDto;
import com.kkhstudy.querydsl.dto.UserDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static com.kkhstudy.querydsl.domain.QPhoto.photo;
import static com.kkhstudy.querydsl.domain.QStudent.student;
import static com.kkhstudy.querydsl.domain.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Student student1 = new Student("member1", 10, teamA);
        Student student2 = new Student("member2", 20, teamA);
        Student student3 = new Student("member3", 30, teamB);
        Student student4 = new Student("member4", 40, teamB);
        em.persist(student1);
        em.persist(student2);
        em.persist(student3);
        em.persist(student4);

        Photo photo1 = new Photo("test1", student1);
        Photo photo2 = new Photo("test2", student1);

        em.persist(photo1);
        em.persist(photo2);
    }

    @Test
    public void startQueyrdsl() {
        Student findStudent = queryFactory
                .select(student)
                .from(student)
                .where(student.username.eq("member1"))
                .fetchOne();
        assertThat(findStudent.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQueyrdsl2() {
        em.flush();
        em.clear();
        List<Student> result =  queryFactory
                .selectDistinct(student)
                .from(student)
                .join(student.photos, photo)
                .join(student.team, team)
                .where(student.username.eq("member1"),
                        student.age.eq(10)
                )
                .fetch();
        assertThat(result.get(0).getPhotos().get(0).getFilePath()).isEqualTo("test1");
    }

    @Test
    public void resultFetch() {
        List<Student> fetch = queryFactory
                .selectFrom(student)
                .fetch();

        Student fetchOne = queryFactory
                .selectFrom(student)
                .fetchOne();

        Student fetchFirst = queryFactory
                .selectFrom(student)
                .fetchFirst();

        // 복잡한 쿼리는 카운트와 조회를 별도로 실행해야한다.
        QueryResults<Student> results = queryFactory
                .selectFrom(student)
                .fetchResults();

        results.getTotal();
        List<Student> content = results.getResults();

        long total = queryFactory
                .selectFrom(student)
                .fetchCount();
    }

    @Test
    public void sort() {
        em.persist(new Student(null, 100));
        em.persist(new Student("member5", 100));
        em.persist(new Student("member6", 100));

        List<Student> result = queryFactory
                .selectFrom(student)
                .where(student.age.eq(100))
                .orderBy(student.age.desc(), student.username.asc().nullsLast())
                .fetch();

        Student student5 = result.get(0);
        Student student6 = result.get(1);
        Student studentNull = result.get(2);

        assertThat(student5.getUsername()).isEqualTo("member5");
        assertThat(student5.getUsername()).isEqualTo("member6");
        assertThat(student5.getUsername()).isNull();
    }

    @Test
    public void paging1() {
        List<Student> result = queryFactory
                .selectFrom(student)
                .orderBy(student.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        QueryResults<Student> queryResults = queryFactory
                .selectFrom(student)
                .orderBy(student.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory
                .select(student.count(),
                        student.age.sum(),
                        student.age.avg(),
                        student.age.max(),
                        student.age.min()
                )
                .from(student)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(student.count())).isEqualTo(4);
        assertThat(tuple.get(student.age.sum())).isEqualTo(100);
        assertThat(tuple.get(student.age.avg())).isEqualTo(25);
        assertThat(tuple.get(student.age.max())).isEqualTo(40);
        assertThat(tuple.get(student.age.min())).isEqualTo(10);
    }

    /*팀의 이름과 각 팀의 평균 연력을 구해라.*/
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, student.age.avg())
                .from(student)
                .join(student.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(student.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(student.age.avg())).isEqualTo(35);
    }

    /*팀 A에 소속되 모든 회원*/
    @Test
    public void join() throws Exception {
        List<Student> result = queryFactory
                .selectFrom(student)
                .join(student.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /*
    세타 조인
    회원의 이름이 팀 이름과 같은 회원 조회
    */
    @Test
    public void theta_join() throws Exception {
        em.persist(new Student("teamA"));
        em.persist(new Student("teamB"));
        em.persist(new Student("teamC"));

        List<Student> result = queryFactory
                .select(student)
                .from(student, team)
                .where(student.username.eq(team.name))
                .fetch();
        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    /*예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조호ㅣ
    JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'*/
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(student, team)
                .from(student)
                .leftJoin(student.team, team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.printf("tuple = " + tuple);
        }
    }

    /*예) 연관관계가 없는 엔티티 외부조인
        회원의 이름이 팀 이름과 같은 대상 외부 조인*/
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Student("teamA"));
        em.persist(new Student("teamB"));
        em.persist(new Student("teamC"));

        List<Tuple> result = queryFactory
                .select(student, team)
                .from(student)
                .leftJoin(team).on(student.username.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.printf("tuple = " + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();
        Student findStudent = queryFactory
                .selectFrom(QStudent.student)
                .where(QStudent.student.username.eq("member1"))
                .fetchOne();

        // 초기환 된 엔티티인지 아닌지 판별
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findStudent.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();
        Student findStudent = queryFactory
                .selectFrom(QStudent.student)
                .join(student.team, team).fetchJoin()
                .where(QStudent.student.username.eq("member1"))
                .fetchOne();

        // 초기환 된 엔티티인지 아닌지 판별
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findStudent.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    // 나이가 가장 많은 회원 조회
    @Test
    public void subQuery() throws Exception {
        QStudent studentSub = new QStudent("studentSub");
        List<Student> result = queryFactory
                .selectFrom(student)
                .where(student.age.eq(
                    select(studentSub.age.max())
                        .from(studentSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    // 나이가 평균 이상인 회원
    @Test
    public void subQueryGoe() throws Exception {
        QStudent studentSub = new QStudent("studentSub");
        List<Student> result = queryFactory
                .selectFrom(student)
                .where(student.age.goe(
                        select(studentSub.age.avg())
                                .from(studentSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(30, 40);
    }

    // 나이가 가장 평균 이상인 회원
    @Test
    public void subQueryIn() throws Exception {
        QStudent studentSub = new QStudent("studentSub");
        List<Student> result = queryFactory
                .selectFrom(student)
                .where(student.age.in(
                        select(studentSub.age)
                                .from(studentSub)
                                .where(studentSub.age.gt(10))
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);
    }

    @Test
    public void selectSubQuery() throws Exception {
        QStudent studentSub = new QStudent("studentSub");
        List<Tuple> result = queryFactory
                .select(student.username,
                        select(studentSub.age.avg())
                                .from(studentSub)
                )
                .from(student)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    // 가급적이면 DB에 CASE 하기보다는 JAVA에서 처리하도록 권장한다.
    @Test
    public void basicCase() throws Exception {
        List<String> result = queryFactory
                .select(student.age
                        .when(10).then("열살")
                        .when(10).then("스무살")
                        .otherwise("기타")
                )
                .from(student)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase() throws Exception {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(student.age.between(0, 20)).then("0 ~ 20살")
                        .when(student.age.between(0, 20)).then("21 ~ 30살")
                        .otherwise("기타")
                )
                .from(student)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void constant() {
        List<Tuple> result = queryFactory
                .select(student.username, Expressions.constant("A"))
                .from(student)
                .fetch();
        for ( Tuple tuple: result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void concat() throws Exception {
        // {username}_{age}
        List<String> result = queryFactory
                .select(student.username.concat("_").concat(student.age.stringValue()))
                .from(student)
                .where(student.username.eq("student1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // Tuple 반환은 repository 외부에 반환되는 것은 권장하지 않는다.
    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> result = queryFactory
                .select(student.username, student.age)
                .from(student)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(student.username);
            Integer age = tuple.get(student.age);
            System.out.println(username);
            System.out.println(age);
        }
    }

    @Test
    public void findDtoByJPQL() throws Exception {
        // 순수 JPQL에서 DTO를 사용하려면 생성자 형식으로 사용해야 한다.
        List<StudentDto> result = em.createQuery("select new com.kkhstudy.querydsl.dto.StudentDto(s.username, s.age) from Student s", StudentDto.class)
                .getResultList();

        for (StudentDto studentDto : result) {
            System.out.println("memberDto = " + studentDto);
        }
    }

    // Setter 대입 방식
    @Test
    public void findDtoBySetter() throws Exception {
        List<StudentDto> result = queryFactory
                .select(Projections.bean(StudentDto.class, student.username, student.age))
                .from(student)
                .fetch();
        for (StudentDto studentDto : result) {
            System.out.println("studentDto = " + studentDto);
        }
    }

    // 필드 직접 대입방식
    @Test
    public void findDtoByField() throws Exception {
        // DTO의 Getter Setter가 필요없는 방식
        List<StudentDto> result = queryFactory
                .select(Projections.fields(StudentDto.class, student.username, student.age))
                .from(student)
                .fetch();
        for (StudentDto studentDto : result) {
            System.out.println("studentDto = " + studentDto);
        }
    }

    // 생성자 대입 방식
    @Test
    public void findDtoByConstructor() throws Exception {
        List<StudentDto> result = queryFactory
                .select(Projections.constructor(StudentDto.class, student.username, student.age))
                .from(student)
                .fetch();
        for (StudentDto studentDto : result) {
            System.out.println("studentDto = " + studentDto);
        }
    }

    // DTO에 대입할때 필요명이 같지 않을때.
    @Test
    public void findDtoByUserDto() throws Exception {
        QStudent studentSub = new QStudent("studentSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        student.username.as("name"), // 일반적인 이름 변경
                        // 서브 쿼리의 이름 변경
                        ExpressionUtils.as(JPAExpressions.select(studentSub.age.max()).from(studentSub), "age")
                ))
                .from(student)
                .fetch();
        for (UserDto userDto : result) {
            System.out.println("studentDto = " + userDto);
        }
    }

    // QueryDSL에서 Dto를 생성해준다.
    // 장점! 안전한 타입 체크가 가능.
    // 생성자 대입 방식과 다른점은 생성자 방식의 경우 타입 에러를 체크해주지 못한다.
    // 런타임 에러가 발생함.
    // 단점! DTO에 QueryDTL에 대한 의존성을 가지게 된다.
    // DTO는 여러 레이어에 걸쳐서 사용되기 때문에 QueryDSL을 사용하지 않게되면 영향이 커진다.
    @Test
    public void findDtoByQueryProjection() throws Exception {
        List<StudentDto> result = queryFactory
                .select(new QStudentDto(student.username, student.age))
                .from(student)
                .fetch();
        for (StudentDto studentDto : result) {
            System.out.println("studentDto = " + studentDto);
        }
    }

    // 동적 쿼리를 사용하는 방법!
    // 1. BooleanBuilder를 사용하는 방법
    @Test
    public void dynamicQuery_BooleanBuilder() throws Exception {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Student> result = searchStudent1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);
    }

    private List<Student> searchStudent1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (usernameCond != null) {
            builder.and(student.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(student.age.eq(ageCond));
        }
        return queryFactory
                .selectFrom(student)
                .where(builder)
                .fetch();
    }
    // 2. Where 다중 파라미터 사용
    @Test
    public void dynamicQuery_WhereParam() throws Exception {
        String usernameParm = "memeber1";
        Integer ageParam = 10;
        List<Student> result = searchStudent2(usernameParm, ageParam);
    }

    private List<Student> searchStudent2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(student)
                .where(allEq(usernameCond, ageCond))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        if (usernameCond == null) {
            return null;
        }
        return student.username.eq(usernameCond);
    }

    private BooleanExpression ageEq(Integer ageCond) {
        if (ageCond == null) {
            return null;
        }
        return student.age.eq(ageCond);
    }
    private Predicate allEq(String usernameCond, Integer ageCond) {
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }

    @Test
    public void bulkUpdate() throws Exception {
        long updateCount = queryFactory
                .update(student)
                .set(student.username, "비회원")
                .where(student.age.lt(28))
                .execute();
        em.flush();
        em.clear();

        List<Student> result = queryFactory.selectFrom(student).fetch();

        for (Student student1 : result) {
            System.out.println("student1 = " + student1);
        }
    }

    @Test
    public void buldAdd() throws Exception {
        long count = queryFactory
                .update(student)
                .set(student.age, student.age.add(1))
                .execute();
    }

    @Test
    public void bulkDelete() throws Exception {
        long count = queryFactory
                .delete(student)
                .where(student.age.gt(18))
                .execute();
    }
    @Test
    public void sqlFunction() throws Exception {
        // H2Dialect에 등록되 함수만 사용가능.
        // 임의의 함수 사용하고 싶을 경우 상속으로 등룍해야한다.
        List<String> result = queryFactory
                .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
                        student.username, "member", "M"))
                .from(student)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void sqlFunction2() throws Exception {
        // 일반적인 함수들은 querydsl이 대부분 내장하고 있다.
        List<String> result = queryFactory
                .select(student.username)
                .from(student)
                //.where(student.username.eq(
                //        Expressions.stringTemplate("function('lower', {0})", student.username)))
                .where(student.username.eq(student.username.lower()))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}