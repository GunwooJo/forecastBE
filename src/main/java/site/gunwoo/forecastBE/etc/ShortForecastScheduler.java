package site.gunwoo.forecastBE.etc;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.gunwoo.forecastBE.dto.PositionDTO;
import site.gunwoo.forecastBE.entity.MemberRegion;
import site.gunwoo.forecastBE.repository.MemberRegionRepository;
import site.gunwoo.forecastBE.repository.ShortForecastRepository;
import site.gunwoo.forecastBE.service.ShortForecastService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//TODO: 00시 ~ 00시 45분 사이에는 하루 전의 baseDate를 사용하고 baseTime도 수정해야함
@Component
@RequiredArgsConstructor
public class ShortForecastScheduler {

    private final ShortForecastService shortForecastService;
    private final ShortForecastRepository shortForecastRepository;
    private final MemberRegionRepository memberRegionRepository;

    /* 1시간마다 초단기예보 데이터 받아오기 */
    @Scheduled(cron = "0 46 * * * ?")
    public void getShortForecastData() {

        List<PositionDTO> positions = new ArrayList<>();

        List<MemberRegion> memberRegions = memberRegionRepository.findAllWithRegion();
        memberRegions.forEach(mr -> {
            short xPos = mr.getRegion().getNx();
            short yPos = mr.getRegion().getNy();
            positions.add(new PositionDTO(xPos, yPos));
        });

        for (PositionDTO position : positions) {
            short xPos = position.getNx();
            short yPos = position.getNy();

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

            shortForecastService.saveShortForecast(currentDate, currentTime, 60, 1, xPos, yPos);
        }
    }

    /* 생성된지 1일이 지난 단기예보 데이터 삭제 */
    @Scheduled(cron = "0 0 4 * * ?")    //0초 0분 4시 매일 매월 모든요일에 실행
    public void deleteForecastData() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        shortForecastRepository.deleteShortForecastByCreatedAtBefore(oneDayAgo);
    }
}
