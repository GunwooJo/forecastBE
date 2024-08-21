package site.gunwoo.forecastBE.repository;

import org.assertj.core.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ShortForecastRepositoryTest {

    @Autowired
    private ShortForecastRepository shortForecastRepository;

    @DisplayName("x좌표, y좌표, baseDate, baseTime을 통해 저장된 단기예보 데이터를 조회한다.")
    @Test
    void findShortForecast() {
        //given
        int x = 62;
        int y = 123;
        LocalDateTime localDateTime = LocalDateTime.of(3000, 1, 1, 20, 0);
        LocalDate baseDate = localDateTime.toLocalDate();
        LocalTime baseTime = localDateTime.toLocalTime();

        //when
        List<ShortForecast> foundForecasts = shortForecastRepository.findShortForecast(baseDate, baseTime, x, y);

        //then
        assertThat(foundForecasts).hasSize(2)
                .extracting("baseDate", "baseTime", "nx", "ny", "id")
                .containsExactlyInAnyOrder(
                        tuple(baseDate, baseTime, 62, 123, 1L),
                        tuple(baseDate, baseTime, 62, 123, 2L)
                );

    }
}
