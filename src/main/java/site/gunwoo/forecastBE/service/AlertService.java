package site.gunwoo.forecastBE.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.gunwoo.forecastBE.dto.AlertSettingDTO;
import site.gunwoo.forecastBE.dto.MailDTO;
import site.gunwoo.forecastBE.entity.Alert;
import site.gunwoo.forecastBE.entity.Member;
import site.gunwoo.forecastBE.entity.ShortForecast;
import site.gunwoo.forecastBE.repository.AlertRepository;
import site.gunwoo.forecastBE.repository.MemberRepository;
import site.gunwoo.forecastBE.repository.ShortForecastRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertService {

    private final ShortForecastRepository shortForecastRepository;
    private final AlertRepository alertRepository;
    private final MailService mailService;
    private final MemberRepository memberRepository;

    /*ShortForecast 중 category의 RN1이 "강수없음"이 아닌 리스트를 먼저 구함. (fcstDate가 어제인 것부터)
    리스트에 대해 반복문을 걸고 nx, ny를 비교하여 Alert를 찾음.
    해당 Alert의 member에게 이메일 발송.
     */
    public void sendForecastAlert(LocalDateTime now) {

        LocalDate yesterday = now.toLocalDate().minusDays(1);
        List<ShortForecast> shortForecasts = shortForecastRepository.findByFcstDateGreaterThanEqual(yesterday); //어제부터의 ShortForecast 리스트

        LocalTime nextHour = now.toLocalTime().plusHours(1).withMinute(0); // ex) 현재 20:14이면 21:00
        LocalTime latest30Min = now.toLocalTime().minusHours(1).withMinute(30); // ex) 현재 20:14이면 18:30

        List<ShortForecast> alertForecasts = shortForecasts.stream()    //비 또는 눈이 오는 ShortForecast 리스트 중 지역별 가장 최신 데이터.
                .filter(sf -> "RN1".equals(sf.getCategory()) &&
                                !"강수없음".equals(sf.getFcstValue()) &&
                                sf.getFcstTime().equals(nextHour) &&
                                sf.getBaseTime().equals(latest30Min)
                        )
                .toList();

        List<Alert> allAlerts = alertRepository.findAll(); // 모든 Alert 리스트

        alertForecasts.forEach(af -> {
            allAlerts.stream()
                    .filter(a -> a.getNx() == af.getNx() && a.getNy() == af.getNy())
                    .forEach(a -> {
                        String regionName = a.getR1() + " " + a.getR2() + " " + a.getR3();
                        String mailTitle = regionName + " 지역에 비 또는 눈이 예상됩니다.";
                        String mailContent = "지역: " + regionName + "\n" +
                                "예상 시간: " + af.getFcstTime().getHour() + "시 " + af.getFcstTime().getMinute() + "분" + "\n" +
                                "예상 강수(우)량: " + af.getFcstValue() + "mm" + "\n";
                                mailService.sendMail(new MailDTO(a.getMember().getEmail(), mailTitle, mailContent));
                    });

        });

    }

    public void setAlert(String userEmail, AlertSettingDTO alertSettingDTO) {

        Member foundMember = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NoResultException("해당 이메일로 등록된 사용자를 찾을 수 없습니다."));

        Alert alert = Alert.builder()
                .r1(alertSettingDTO.getR1())
                .r2(alertSettingDTO.getR2())
                .r3(alertSettingDTO.getR3())
                .nx(alertSettingDTO.getNx())
                .ny(alertSettingDTO.getNy())
                .build();

        alert.changeMember(foundMember);
        alertRepository.save(alert);
    }
}
