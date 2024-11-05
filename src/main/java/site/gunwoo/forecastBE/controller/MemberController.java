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
import site.gunwoo.forecastBE.dto.MemberJoinDTO;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.dto.MemberDTO;
import site.gunwoo.forecastBE.repository.RegionRepository;
import site.gunwoo.forecastBE.service.ShortForecastService;
import site.gunwoo.forecastBE.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ShortForecastService shortForecastService;
    private final RegionRepository regionRepository;

    @PostMapping("/user/join")
    public ResponseEntity<ResponseDTO> join(@RequestBody @Valid MemberJoinDTO memberJoinDTO) {
        memberService.join(memberJoinDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO("회원가입에 성공했습니다.", null));
    }

    @PostMapping("/user/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid MemberDTO memberDTO, HttpSession session) {

        try {
            memberService.login(memberDTO, session);
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
            shortForecastService.saveShortForecast("20240828", "1700", 1000, 1, 62, 123);
            shortForecastService.saveShortForecast("20240828", "1700", 1000, 1, 55, 123);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO("성공", null));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 에러가 발생했습니다: " + e.getMessage(), null));
        }

    }
}
