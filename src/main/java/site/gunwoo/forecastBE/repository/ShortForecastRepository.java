package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import site.gunwoo.forecastBE.entity.ShortForecast;

import java.time.LocalDateTime;
import java.util.List;


public interface ShortForecastRepository extends JpaRepository<ShortForecast, Long> {

    @Transactional
    void deleteShortForecastByCreatedAtBefore(LocalDateTime dateTime);
}
