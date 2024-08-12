package site.gunwoo.forecastBE.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.gunwoo.forecastBE.dto.MemberJoinDTO;
import site.gunwoo.forecastBE.entity.Member;
import site.gunwoo.forecastBE.service.MemberService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @DisplayName("이메일로 회원을 조회한다.")
    @Test
    void test() {
        //given
        List<String> regions = List.of("테스트");
        MemberJoinDTO member1 = MemberJoinDTO.builder()
                .email("test1@naver.com")
                .password("gunwoo1234!")
                .regions(regions)
                .build();
        memberService.join(member1);

        //when
        Member foundMember = memberRepository.findByEmail(member1.getEmail()).get();

        //then
        assertThat(foundMember.getEmail()).isEqualTo(member1.getEmail());

    }
}
