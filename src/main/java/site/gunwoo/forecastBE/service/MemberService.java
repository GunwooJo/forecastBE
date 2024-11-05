package site.gunwoo.forecastBE.service;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.dto.MemberDTO;
import site.gunwoo.forecastBE.dto.MemberJoinDTO;
import site.gunwoo.forecastBE.entity.Member;
import site.gunwoo.forecastBE.entity.MemberRegion;
import site.gunwoo.forecastBE.entity.Region;
import site.gunwoo.forecastBE.repository.MemberRepository;
import site.gunwoo.forecastBE.repository.RegionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void join(MemberJoinDTO memberJoinDTO) {

        if(isEmailDuplicated(memberJoinDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + memberJoinDTO.getEmail());
        }

        String encodedPw = passwordEncoder.encode(memberJoinDTO.getPassword());

        List<MemberRegion> memberRegions = new ArrayList<>();

        memberJoinDTO.getRegions()
                        .forEach(regionNameDTO -> {

                            Region foundRegion = regionRepository.findByR1AndR2AndR3(regionNameDTO.getR1(), regionNameDTO.getR2(), regionNameDTO.getR3())
                                    .orElseThrow(() -> new NoResultException("해당 지역이 존재하지 않습니다: " + regionNameDTO.getR1() + " " + regionNameDTO.getR2() + " " + regionNameDTO.getR3()));
                            MemberRegion memberRegion = MemberRegion.createMemberRegion(foundRegion);
                            memberRegions.add(memberRegion);
                        });

        Member member = Member.builder()
                .email(memberJoinDTO.getEmail())
                .password(encodedPw)
                .build();

        member.changeMemberRegions(memberRegions);

        memberRepository.save(member);

    }

    private boolean isEmailDuplicated(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public void login(MemberDTO userDTO, HttpSession session) {

        Member foundUser = memberRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new NoResultException("해당 이메일을 가진 사용자가 없습니다.: " + userDTO.getEmail()));

        if (passwordEncoder.matches(userDTO.getPassword(), foundUser.getPassword())) {
            session.setAttribute("loggedInUser", foundUser.getEmail());
            session.setMaxInactiveInterval(60 * 30); // 초 단위
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
