package site.gunwoo.forecastBE.controller;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.dto.UserDTO;
import site.gunwoo.forecastBE.service.ShortForecastService;
import site.gunwoo.forecastBE.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ShortForecastService shortForecastService;

    @PostMapping("/user/join")
    public ResponseEntity<ResponseDTO> join(@RequestBody @Valid UserDTO userDTO) {

        try {
            userService.join(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO("회원가입에 성공했습니다.", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(e.getMessage(), null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("회원가입에 실패했습니다: " + e.getMessage(), null));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid UserDTO userDTO, HttpSession session) {

        try {
            userService.login(userDTO, session);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("로그인에 성공했습니다.", null));

        } catch (NoResultException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO("존재하지 않는 사용자거나 비밀번호가 틀렸습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 에러가 발생했습니다: " + e.getMessage(), null));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ResponseDTO> test() {

        try {
            shortForecastService.saveShortForecast("20240726", "0200", 300, 1, 62, 123);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO("단기예보 저장완료.", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 에러가 발생했습니다: " + e.getMessage(), null));
        }

    }
}
