package site.gunwoo.forecastBE.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.gunwoo.forecastBE.dto.ResponseDTO;
import site.gunwoo.forecastBE.dto.SimpleShortForecastDTO;
import site.gunwoo.forecastBE.entity.ShortForecast;
import site.gunwoo.forecastBE.service.ShortForecastService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ShortForecastController {

    private final ShortForecastService shortForecastService;

    @GetMapping("/short-forecast")
    public ResponseEntity<ResponseDTO> getShortForecastByPos(
            @RequestParam("x-pos") int xPos,
            @RequestParam("y-pos") int yPos) {

        try {
            List<ShortForecast> forecasts = shortForecastService.findShortForecast(LocalDateTime.now(), xPos, yPos);
            List<SimpleShortForecastDTO> forecastDTOS = forecasts.stream()
                    .map(SimpleShortForecastDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO("조회 성공", forecastDTOS));

        } catch (NoSuchElementException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(e.getMessage(), null));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseDTO(e.getMessage(), null));
        }
    }
}
