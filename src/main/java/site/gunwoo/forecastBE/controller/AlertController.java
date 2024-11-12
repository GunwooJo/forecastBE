package site.gunwoo.forecastBE.controller;

import jakarta.persistence.NoResultException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import site.gunwoo.forecastBE.dto.AlertDTO;
import site.gunwoo.forecastBE.dto.AlertSettingDTO;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.entity.Member;
import site.gunwoo.forecastBE.repository.MemberRepository;
import site.gunwoo.forecastBE.service.AlertService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final MemberRepository memberRepository;

    @PostMapping("/alert/setting")
    public ResponseEntity<ResponseDTO> setAlert(@RequestBody @Valid AlertSettingDTO alertSettingDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        alertService.setAlert(userEmail, alertSettingDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("날씨 알림 설정 완료", null));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ResponseDTO> showAlerts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Member logginedMember = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NoResultException("해당 이메일로 가입된 회원이 없습니다: " + userEmail));

        List<AlertDTO> alertDTOS = logginedMember.getAlerts().stream()
                                        .map(a -> AlertDTO.builder()
                                                .id(a.getId())
                                                .r1(a.getR1())
                                                .r2(a.getR2())
                                                .r3(a.getR3())
                                                .nx(a.getNx())
                                                .ny(a.getNy())
                                                .build()
                                        ).toList();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("지역목록 조회 성공", alertDTOS));
    }

    @DeleteMapping("/alert/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteAlert(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        alertService.deleteAlert(userEmail, id);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("날씨 알림 삭제 완료", null));
    }
}
