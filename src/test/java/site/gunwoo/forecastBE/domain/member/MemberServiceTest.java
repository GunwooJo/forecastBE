package site.gunwoo.forecastBE.domain.member;

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

    @DisplayName("이미 가입된 회원의 이메일로 회원가입을 시도하면 예외를 발생시킨다.")
    @Test
    void joinWithExistingEmail() {
        //given
        List<String> regions = List.of("테스트");
        MemberJoinDTO member1 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .regions(regions)
                .build();
        memberService.join(member1);

        MemberJoinDTO member2 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .regions(regions)
                .build();
        //when, then
        assertThatThrownBy(()->memberService.join(member2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다: " + member2.getEmail());

    }

    @DisplayName("region 테이블에 존재하지 않는 지역 이름으로 가입을 시도하면 예외를 발생시킨다.")
    @Test
    void joinWithNonExistentRegion() {
        //given
        List<String> regions = List.of("test", "존재하지않는지역명");
        MemberJoinDTO member = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .regions(regions)
                .build();

        //when, then
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("존재하지 않는 지역입니다.");
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void join() {
        //given
        List<String> regions = List.of("테스트");
        MemberJoinDTO member1 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .regions(regions)
                .build();
        memberService.join(member1);

        MemberJoinDTO member2 = MemberJoinDTO.builder()
                .email("unique@naver.com")
                .password("gunwoo1234!")
                .regions(regions)
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
