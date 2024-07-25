package site.gunwoo.forecastBE.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import site.gunwoo.forecastBE.config.ForecastConstants;
import site.gunwoo.forecastBE.dto.ShortForeCastResponseDTO;
import site.gunwoo.forecastBE.entity.ShortForecast;
import site.gunwoo.forecastBE.exception.CustomClientException;
import site.gunwoo.forecastBE.exception.CustomServerException;
import site.gunwoo.forecastBE.repository.ShortForecastRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShortForecastService {

    private final WebClient.Builder webClientBuilder;
    private final ShortForecastRepository shortForecastRepository;

    //강수확률을 알기 위한 단기예보 조회 후 저장.
    //조회하는 코드와 저장하는 코드를 분리하자.
    //아니면 category로 분류하지 말고 그냥 다 저장할까?
    public void saveShortForecast(String baseDate, String baseTime, int numOfRows, int pageNo, int nx, int ny) {

        WebClient webClient = webClientBuilder
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
                .build();

        String uri = "/getVilageFcst";

        try {
            ShortForeCastResponseDTO response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParam("serviceKey", ForecastConstants.serviceKey)
                            .queryParam("numOfRows", numOfRows)
                            .queryParam("pageNo", pageNo)
                            .queryParam("dataType", "JSON")
                            .queryParam("base_date", baseDate)
                            .queryParam("base_time", baseTime)
                            .queryParam("nx", nx)
                            .queryParam("ny", ny)
                            .build())
                    .retrieve()
                    .bodyToMono(ShortForeCastResponseDTO.class)
                    .block();

            if(response != null) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

                List<ShortForeCastResponseDTO.Response.Body.Items.ForecastItem> itemList = response.getResponse().getBody().getItems().getItem();

                List<ShortForecast> forecastItems = itemList.stream()
                        .filter(forecastItem -> "POP".equals(forecastItem.getCategory()))
                        .map(forecastItem -> {
                            return ShortForecast.builder()
                                    .baseDate(LocalDate.parse(forecastItem.getBaseDate(), dateFormatter))
                                    .baseTime(LocalTime.parse(forecastItem.getBaseTime(), timeFormatter))
                                    .category(forecastItem.getCategory())
                                    .fcstDate(LocalDate.parse(forecastItem.getFcstDate(), dateFormatter))
                                    .fcstTime(LocalTime.parse(forecastItem.getFcstTime(), timeFormatter))
                                    .fcstValue(Integer.parseInt(forecastItem.getFcstValue()))
                                    .nx(forecastItem.getNx())
                                    .ny(forecastItem.getNy())
                                    .build();
                        }).toList();
                shortForecastRepository.saveAll(forecastItems);
            }

        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode().is4xxClientError()) {
                throw new CustomClientException("단기예보 api 호출 중 client 에러 발생", ex);
            } else if (ex.getStatusCode().is5xxServerError()) {
                throw new CustomServerException("단기예보 api 호출 중 server 에러 발생", ex);
            } else {
                throw new RuntimeException("예상치 못한 에러 발생", ex);
            }
        }


    }
}
