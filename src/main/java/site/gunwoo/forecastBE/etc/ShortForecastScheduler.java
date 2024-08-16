package site.gunwoo.forecastBE.etc;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.gunwoo.forecastBE.repository.RegionRepository;
import site.gunwoo.forecastBE.repository.ShortForecastRepository;
import site.gunwoo.forecastBE.service.ShortForecastService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShortForecastScheduler {

    private final RegionRepository regionRepository;
    private final ShortForecastService shortForecastService;
    private final ShortForecastRepository shortForecastRepository;

    /* 3시간마다 단기예보 데이터 받아오기 */
    @Scheduled(cron = "0 12 2,5,8,11,14,17,20,23 * * ?")
    public void getShortForecastData() {

        List<Object[]> positionList = regionRepository.findDistinctByXPosAndYPos();
        for (Object[] position : positionList) {
            short xPos = (short) position[0];
            short yPos = (short) position[1];

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String currentDate = LocalDate.now().format(dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
            String currentTime = LocalTime.now().withMinute(0).format(timeFormatter); // 분을 0으로 설정

            shortForecastService.saveShortForecast(currentDate, currentTime, 36, 1, xPos, yPos);
        }
    }

    /* 생성된지 1일이 지난 단기예보 데이터 삭제 */
    @Scheduled(cron = "0 0 4 * * ?")    //0초 0분 4시 매일 매월 모든요일에 실행
    public void deleteForecastData() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        shortForecastRepository.deleteShortForecastByCreatedAtBefore(oneDayAgo);
    }
}
