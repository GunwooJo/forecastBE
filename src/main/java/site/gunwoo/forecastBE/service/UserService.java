package site.gunwoo.forecastBE.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.dto.UserJoinDTO;
import site.gunwoo.forecastBE.entity.User;
import site.gunwoo.forecastBE.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void join(UserJoinDTO userJoinDTO) {

        if(isEmailDuplicated(userJoinDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userJoinDTO.getEmail());
        }

        String encodedPw = passwordEncoder.encode(userJoinDTO.getPassword());
        User user = User.builder()
                .email(userJoinDTO.getEmail())
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
}
