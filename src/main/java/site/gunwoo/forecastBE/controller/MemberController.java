package site.gunwoo.forecastBE.controller;

import jakarta.persistence.NoResultException;
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
import site.gunwoo.forecastBE.service.AlertService;
import site.gunwoo.forecastBE.service.ShortForecastService;
import site.gunwoo.forecastBE.service.MemberService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ShortForecastService shortForecastService;
    private final RegionRepository regionRepository;
    private final AlertService alertService;

    @PostMapping("/user/join")
    public ResponseEntity<ResponseDTO> join(@RequestBody @Valid MemberJoinDTO memberJoinDTO) {
        memberService.join(memberJoinDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO("회원가입에 성공했습니다.", null));
    }

    @PostMapping("/user/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid MemberDTO memberDTO) {

        try {
            String accessToken = memberService.login(memberDTO);
            Map<String, String> tokenRes = new HashMap<>();
            tokenRes.put("accessToken", accessToken);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("로그인에 성공했습니다.", tokenRes));

        } catch (NoResultException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO("존재하지 않는 사용자거나 비밀번호가 틀렸습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO("서버 에러가 발생했습니다: " + e.getMessage(), null));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ResponseDTO> test() {
        // 특정 시간 기준으로 메일 전송 테스트
        LocalDateTime tempNow = LocalDateTime.of(2024, 11, 10, 20, 45);
        alertService.sendForecastAlert(tempNow);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("테스트 완료", null));
    }
}
