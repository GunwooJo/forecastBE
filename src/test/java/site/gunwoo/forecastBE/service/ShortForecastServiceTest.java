package site.gunwoo.forecastBE.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.gunwoo.forecastBE.entity.ShortForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

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
}
