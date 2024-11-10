package site.gunwoo.forecastBE.etc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.gunwoo.forecastBE.dto.PositionDTO;
import site.gunwoo.forecastBE.repository.AlertRepository;
import site.gunwoo.forecastBE.repository.MemberRegionRepository;
import site.gunwoo.forecastBE.repository.ShortForecastRepository;
import site.gunwoo.forecastBE.service.AlertService;
import site.gunwoo.forecastBE.service.ShortForecastService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShortForecastScheduler {

    private final ShortForecastService shortForecastService;
    private final ShortForecastRepository shortForecastRepository;
    private final MemberRegionRepository memberRegionRepository;
    private final AlertService alertService;
    private final AlertRepository alertRepository;

    /* 1시간마다 초단기예보 데이터 받아오기 */
    @Scheduled(cron = "0 46 * * * ?")
    public void saveShortForecastData() {

        List<PositionDTO> positions = new ArrayList<>(); // 사용자가 알림을 등록한 지역의 위치 리스트

        alertRepository.findAll().forEach(alert ->
            positions.add(new PositionDTO(alert.getNx(), alert.getNy()))
        );

        for (PositionDTO position : positions) {
            short nx = position.getNx();
            short ny = position.getNy();
            log.info("nx: " + nx + ", ny: " + ny);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String currentDate = LocalDate.now().format(dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
            LocalTime now = LocalTime.now();
            LocalTime closestPast46Minute = now.withMinute(46).isBefore(now) ? now.withMinute(46) : now.withMinute(46).minusHours(1);

            // 오전 12시 ~ 12시 45분 사이의 경우, 날짜를 하루 전으로 설정
            if (now.isBefore(LocalTime.of(0, 46))) {
                currentDate = LocalDate.now().minusDays(1).format(dateFormatter);
            }

            String currentTime = closestPast46Minute.format(timeFormatter);

            shortForecastService.saveShortForecast(currentDate, currentTime, 60, 1, nx, ny);
        }
    }

    /* 생성된지 1일이 지난 단기예보 데이터 삭제 */
    @Scheduled(cron = "0 0 4 * * ?")    //0초 0분 4시 매일 매월 모든요일에 실행
    public void deleteForecastData() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        shortForecastRepository.deleteShortForecastByCreatedAtBefore(oneDayAgo);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void sendAlert() {
        alertService.sendForecastAlert(LocalDateTime.now());
    }
}
