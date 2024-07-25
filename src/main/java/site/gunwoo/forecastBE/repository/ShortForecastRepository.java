package site.gunwoo.forecastBE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.gunwoo.forecastBE.entity.ShortForecast;


public interface ShortForecastRepository extends JpaRepository<ShortForecast, Long> {
}
