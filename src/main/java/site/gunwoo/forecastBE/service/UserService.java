package site.gunwoo.forecastBE.service;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.dto.UserDTO;
import site.gunwoo.forecastBE.entity.User;
import site.gunwoo.forecastBE.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void join(UserDTO userDTO) {

        if(isEmailDuplicated(userDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userDTO.getEmail());
        }

        String encodedPw = passwordEncoder.encode(userDTO.getPassword());
        User user = User.builder()
                .email(userDTO.getEmail())
                .password(encodedPw)
                .build();

        userRepository.save(user);

    }

    private boolean isEmailDuplicated(String email) {

        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            return false;
        }
        return true;
    }

    public void login(UserDTO userDTO, HttpSession session) {

        User foundUser = userRepository.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new NoResultException("해당 이메일을 가진 사용자가 없습니다.: " + userDTO.getEmail()));

        if (passwordEncoder.matches(userDTO.getPassword(), foundUser.getPassword())) {
            session.setAttribute("loggedInUser", foundUser.getEmail());
            session.setMaxInactiveInterval(60 * 30); // 초 단위
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
