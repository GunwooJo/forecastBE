package site.gunwoo.forecastBE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import site.gunwoo.forecastBE.config.ForecastConstants;
import site.gunwoo.forecastBE.dto.MailDTO;
import site.gunwoo.forecastBE.dto.ShortForeCastResponseDTO;
import site.gunwoo.forecastBE.entity.ShortForecast;
import site.gunwoo.forecastBE.exception.ShortForecastApiException;
import site.gunwoo.forecastBE.exception.ShortForecastException;
import site.gunwoo.forecastBE.repository.ShortForecastRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ShortForecastService {

    private final WebClient.Builder webClientBuilder;
    private final ShortForecastRepository shortForecastRepository;
    private final MailService mailService;
    private static final String adminEmail = "kanggi1997@gmail.com";

    //강수확률을 알기 위한 초단기예보 조회 후 저장. (category가 RN1, PTY인 항목만)
    public void saveShortForecast(String baseDate, String baseTime, int numOfRows, int pageNo, int nx, int ny) {

        WebClient webClient = webClientBuilder
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String uri = "/getUltraSrtFcst"; //초단기예보 조회

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

            if (response == null) {
                throw new ShortForecastException("단기예보 API 요청에 대한 응답을 받지 못했습니다");
            }

            if (!"00".equals(response.getResponse().getHeader().getResultCode())) {   //00은 정상 응답 시 받는 code.

                MailDTO mailDTO = setMailFormat(response);
                mailService.sendMail(mailDTO);
                throw new ShortForecastException(response.getResponse().getHeader().getResultMsg());
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

            List<ShortForeCastResponseDTO.Response.Body.Items.ForecastItem> itemList = response.getResponse().getBody().getItems().getItem();

            List<ShortForecast> forecastItems = itemList.stream()
                    .filter(forecastItem -> "RN1".equals(forecastItem.getCategory()) || "PTY".equals(forecastItem.getCategory()))
                    .map(forecastItem ->
                        ShortForecast.builder()
                            .baseDate(LocalDate.parse(forecastItem.getBaseDate(), dateFormatter))
                            .baseTime(LocalTime.parse(forecastItem.getBaseTime(), timeFormatter))
                            .category(forecastItem.getCategory())
                            .fcstDate(LocalDate.parse(forecastItem.getFcstDate(), dateFormatter))
                            .fcstTime(LocalTime.parse(forecastItem.getFcstTime(), timeFormatter))
                            .fcstValue(forecastItem.getFcstValue())
                            .nx(forecastItem.getNx())
                            .ny(forecastItem.getNy())
                            .build())
                    .toList();
            shortForecastRepository.saveAll(forecastItems);

        } catch (WebClientResponseException ex) {

            log.error("오류 발생: ", ex);

            String emailTitle = "단기예보 API 호출 중 오류 발생!";
            String emailMessage = "에러코드: " + ex.getStatusCode() + "\n" + "메시지: " + ex.getMessage();
            MailDTO mailDTO = MailDTO.builder()
                    .address(adminEmail)
                    .title(emailTitle)
                    .message(emailMessage)
                    .build();
            mailService.sendMail(mailDTO);

            if (ex.getStatusCode().is4xxClientError()) {
                throw new ShortForecastException("단기예보 api 호출 중 client 에러 발생", ex);

            } else if (ex.getStatusCode().is5xxServerError()) {
                throw new ShortForecastException("단기예보 api 호출 중 server 에러 발생", ex);

            } else {
                throw new RuntimeException("예상치 못한 에러 발생", ex);
            }
        }


    }

    private static MailDTO setMailFormat(ShortForeCastResponseDTO response) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String dateTimeNow = LocalDateTime.now().format(formatter);

        String emailTitle = "단기예보 API 문제 발생!";
        String emailMessage = "에러코드: " + response.getResponse().getHeader().getResultCode() + "\n"
                + "메시지: " + response.getResponse().getHeader().getResultMsg() + "\n" +
                "에러발생 시각: " + dateTimeNow;

        MailDTO mailDTO = MailDTO.builder()
                .address(adminEmail)
                .title(emailTitle)
                .message(emailMessage)
                .build();
        return mailDTO;
    }

    public List<ShortForeCastResponseDTO.Response.Body.Items.ForecastItem> callShortForecastApi(LocalDateTime now, int nx, int ny) {

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
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmm");

        String inputBaseDate = baseDate.format(dateFormatter);
        String inputBaseTime = closestPastTime.format(timeFormatter);

        WebClient webClient = webClientBuilder
                .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
                .build();

        String uri = "/getVilageFcst";

        try {
            ShortForeCastResponseDTO response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(uri)
                            .queryParam("serviceKey", ForecastConstants.serviceKey)
                            .queryParam("numOfRows", 36)
                            .queryParam("pageNo", 1)
                            .queryParam("dataType", "JSON")
                            .queryParam("base_date", inputBaseDate)
                            .queryParam("base_time", inputBaseTime)
                            .queryParam("nx", nx)
                            .queryParam("ny", ny)
                            .build())
                    .retrieve()
                    .bodyToMono(ShortForeCastResponseDTO.class)
                    .block();

            if (response == null) {
                throw new ShortForecastException("단기예보 API 요청에 대한 응답을 받지 못했습니다");
            }

            if (!"00".equals(response.getResponse().getHeader().getResultCode())) {   //00은 정상 응답 시 받는 code.

                throw new ShortForecastApiException("단기예보 API 호출 중 문제 발생!", response.getResponse().getHeader().getResultCode(), response.getResponse().getHeader().getResultMsg());
            }

            return response.getResponse().getBody().getItems().getItem();

        } catch (WebClientResponseException ex) {

            if (ex.getStatusCode().is4xxClientError()) {
                log.error(ex.getMessage());
                throw new ShortForecastApiException("단기예보 api 호출 중 client 에러 발생", ex);

            } else if (ex.getStatusCode().is5xxServerError()) {
                log.error(ex.getMessage());
                throw new ShortForecastApiException("단기예보 api 호출 중 server 에러 발생", ex);

            } else {
                log.error(ex.getMessage());
                throw new RuntimeException("예상치 못한 에러 발생", ex);
            }
        }
    }

    public List<ShortForecast> findShortForecast(LocalDateTime now, int nx, int ny) {
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

        List<ShortForecast> forecasts = shortForecastRepository.findShortForecast(baseDate, closestPastTime, nx, ny);
        if (forecasts.isEmpty()) {
            throw new NoSuchElementException("해당하는 단기예보 데이터가 없습니다.");
        }
        return forecasts;
    }
}
