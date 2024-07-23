package site.gunwoo.forecastBE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ForecastBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForecastBeApplication.class, args);
	}

}
