package site.gunwoo.forecastBE.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.dto.MemberDTO;
import site.gunwoo.forecastBE.dto.MemberJoinDTO;
import site.gunwoo.forecastBE.entity.Alert;
import site.gunwoo.forecastBE.entity.Member;
import site.gunwoo.forecastBE.config.auth.JwtUtil;
import site.gunwoo.forecastBE.repository.MemberRepository;
import site.gunwoo.forecastBE.repository.RegionRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RegionRepository regionRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void join(MemberJoinDTO memberJoinDTO) {

        if(isEmailDuplicated(memberJoinDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + memberJoinDTO.getEmail());
        }

        String encodedPw = passwordEncoder.encode(memberJoinDTO.getPassword());

        Member member = Member.builder()
                .email(memberJoinDTO.getEmail())
                .password(encodedPw)
                .build();

        List<Alert> alerts = new ArrayList<>();
        memberJoinDTO.getAlerts()
                .forEach(alertDTO -> {
                    regionRepository.findByR1AndR2AndR3AndNxAndNy(
                                    alertDTO.getR1(),
                                    alertDTO.getR2(),
                                    alertDTO.getR3(),
                                    alertDTO.getNx(),
                                    alertDTO.getNy())
                            .orElseThrow(() -> new NoResultException("해당 지역이 존재하지 않습니다: " + alertDTO.getR1() + " " + alertDTO.getR2() + " " + alertDTO.getR3()));

                    Alert alert = Alert.builder()
                            .r1(alertDTO.getR1())
                            .r2(alertDTO.getR2())
                            .r3(alertDTO.getR3())
                            .nx(alertDTO.getNx())
                            .ny(alertDTO.getNy())
                            .build();
                    alerts.add(alert);
                });

        member.changeAlerts(alerts);
        memberRepository.save(member);

    }

    private boolean isEmailDuplicated(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public String login(MemberDTO userDTO) {

        Member foundUser = memberRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new NoResultException("해당 이메일을 가진 사용자가 없습니다.: " + userDTO.getEmail()));

        if (passwordEncoder.matches(userDTO.getPassword(), foundUser.getPassword())) {
            return jwtUtil.createAccessToken(new MemberDTO(foundUser.getEmail(), foundUser.getPassword()));

        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
