package site.gunwoo.forecastBE.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.gunwoo.forecastBE.dto.ShortForeCastResponseDTO;
import site.gunwoo.forecastBE.entity.ShortForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ShortForecastServiceTest {

    @Autowired
    private ShortForecastService shortForecastService;

    @DisplayName("현재 시각 정보와 x좌표, y좌표를 파라미터로 넘기면 가장 최근에 저장된 단기예보 데이터를 제공해준다.")
    @Test
    void findShortForecast() {
        //given
        LocalDateTime localDateTime = LocalDateTime.of(3000, 1, 1, 23, 14);
        LocalDate localDate = localDateTime.toLocalDate();
        int x = 62;
        int y = 123;

        //when
        List<ShortForecast> foundForecasts = shortForecastService.findShortForecast(localDateTime, x, y);

        //then
        assertThat(foundForecasts).hasSize(1)
                .extracting("baseDate", "baseTime", "nx", "ny")
                .containsExactlyInAnyOrder(
                        tuple(localDate, LocalTime.of(23, 0), 62, 123)
                );
    }

    @DisplayName("단기예보 데이터가 db에 없을 경우 예외를 발생시킨다.")
    @Test
    void findShortForecastNoResult() {
        //given
        LocalDateTime localDateTime = LocalDateTime.of(3500, 1, 1, 23, 14);
        int x = 62;
        int y = 123;

        //when, then
        assertThatThrownBy(() -> shortForecastService.findShortForecast(localDateTime, x, y))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당하는 단기예보 데이터가 없습니다.");
    }

    @DisplayName("단기예보 API를 호출하여 단기예보 데이터를 응답받는다.")
    @Test
    void test() {
        //given
        /*
        2,5,8,11,14,17,20,23시 중 현재 시각으로부터 가장 가까운 시각 정보를 구한 후
        시각 정보를 활용해 단기예보 API를 호출한다.
         */
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        List<LocalTime> targetTimes = Arrays.asList(
                LocalTime.of(2, 13),
                LocalTime.of(5, 13),
                LocalTime.of(8, 13),
                LocalTime.of(11, 13),
                LocalTime.of(14, 13),
                LocalTime.of(17, 13),
                LocalTime.of(20, 13),
                LocalTime.of(23, 13)
        );

        LocalTime closestPastTime = null;

        if(currentTime.isAfter(LocalTime.of(23, 13)) || currentTime.isBefore(LocalTime.of(2,13))) {
            closestPastTime = LocalTime.of(23,0);

        } else {
            for (LocalTime targetTime : targetTimes) {
                if (targetTime.isBefore(currentTime)) {
                    if (closestPastTime == null || targetTime.isAfter(closestPastTime)) {
                        closestPastTime = targetTime.minusMinutes(13);
                    }
                }
            }
        }

        LocalDate baseDate = currentDate;
        if(currentTime.isAfter(LocalTime.of(0,0)) && currentTime.isBefore(LocalTime.of(2, 0))) {
            baseDate = currentDate.minusDays(1);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hhmm");

        String baseDateStr = baseDate.format(dateFormatter);
        String baseTimeStr = closestPastTime.format(timeFormatter);

        int nx = 62;
        int ny = 123;

        //when
        List<ShortForeCastResponseDTO.Response.Body.Items.ForecastItem> forecast = shortForecastService.callShortForecastApi(baseDateStr, baseTimeStr, 2, 1, nx, ny);

        //then
        assertThat(forecast).hasSize(2)
                .extracting("baseDate", "baseTime", "nx", "ny")
                .containsExactlyInAnyOrder(
                        tuple(baseDateStr, baseTimeStr, nx, ny),
                        tuple(baseDateStr, baseTimeStr, nx, ny)
                );

    }
}
