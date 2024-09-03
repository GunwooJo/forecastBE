package site.gunwoo.forecastBE.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.gunwoo.forecastBE.dto.MemberJoinDTO;
import site.gunwoo.forecastBE.repository.MemberRepository;
import site.gunwoo.forecastBE.service.MemberService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @DisplayName("이미 가입된 회원의 이메일로 회원가입을 시도하면 예외를 발생시킨다.")
    @Test
    void joinWithExistingEmail() {
        //given
        MemberJoinDTO member1 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .build();
        memberService.join(member1);

        MemberJoinDTO member2 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .build();
        //when, then
        assertThatThrownBy(()->memberService.join(member2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다: " + member2.getEmail());

    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void join() {
        //given
        MemberJoinDTO member1 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .build();
        memberService.join(member1);

        MemberJoinDTO member2 = MemberJoinDTO.builder()
                .email("unique@naver.com")
                .password("gunwoo1234!")
                .build();
        //when
        memberService.join(member2);

        //then
        memberRepository.findByEmail(member2.getEmail())
                .ifPresentOrElse(
                        (foundMember)-> assertThat(foundMember.getEmail()).isEqualTo("unique@naver.com"),
                        ()->fail("다음 이메일을 가진 회원을 찾지 못함: " + member2.getEmail()));
    }
}
