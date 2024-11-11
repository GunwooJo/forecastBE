package site.gunwoo.forecastBE.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.gunwoo.forecastBE.dto.AlertSettingDTO;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.service.AlertService;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/alert/setting")
    public ResponseEntity<ResponseDTO> setAlert(@RequestBody @Valid AlertSettingDTO alertSettingDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        alertService.setAlert(userEmail, alertSettingDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("날씨 알림 설정 완료", null));
    }
}
