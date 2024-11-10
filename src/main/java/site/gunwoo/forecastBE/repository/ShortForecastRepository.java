package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.entity.ShortForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShortForecastRepository extends JpaRepository<ShortForecast, Long> {

    @Transactional
    void deleteShortForecastByCreatedAtBefore(LocalDateTime dateTime);

    @Query("select s from ShortForecast s where s.baseDate = :baseDate and s.baseTime = :baseTime and s.nx = :nx and s.ny = :ny")
    List<ShortForecast> findShortForecast(LocalDate baseDate, LocalTime baseTime, int nx, int ny);

    List<ShortForecast> findByFcstDateGreaterThanEqual(LocalDate fcstDate);
}
