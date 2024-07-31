package site.gunwoo.forecastBE.service;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.dto.MemberDTO;
import site.gunwoo.forecastBE.entity.Member;
import site.gunwoo.forecastBE.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void join(MemberDTO userDTO) {

        if(isEmailDuplicated(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userDTO.getEmail());
        }

        String encodedPw = passwordEncoder.encode(userDTO.getPassword());
        Member user = Member.builder()
                .email(userDTO.getEmail())
                .password(encodedPw)
                .build();

        memberRepository.save(user);

    }

    private boolean isEmailDuplicated(String email) {

        Optional<Member> byEmail = memberRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            return false;
        }
        return true;
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