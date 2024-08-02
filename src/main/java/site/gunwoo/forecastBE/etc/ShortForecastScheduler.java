package site.gunwoo.forecastBE.etc;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.gunwoo.forecastBE.repository.RegionRepository;
import site.gunwoo.forecastBE.service.ShortForecastService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShortForecastScheduler {

    private final RegionRepository regionRepository;
    private final ShortForecastService shortForecastService;

    /* 3시간마다 단기예보 데이터 받아오기 */
    @Scheduled(cron = "0 12 2,5,8,11,14,17,22,23 * * ?")
    public void getShortForecastData() {

        List<Object[]> positionList = regionRepository.findDistinctByXPosAndYPos();
        for (Object[] position : positionList) {
            short xPos = (short) position[0];
            short yPos = (short) position[1];

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String currentDate = LocalDate.now().format(dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");
            String currentTime = LocalTime.now().withMinute(0).format(timeFormatter); // 분을 0으로 설정

            shortForecastService.saveShortForecast(currentDate, currentTime, 1000, 1, xPos, yPos);
        }
    }
}
