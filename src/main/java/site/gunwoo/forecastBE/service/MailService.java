package site.gunwoo.forecastBE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import site.gunwoo.forecastBE.dto.MailDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendMail(MailDTO mailDTO){
        log.info("받는 사람: {}", mailDTO.getAddress());
        log.info("메일 제목: {}", mailDTO.getTitle());
        log.info("메일 내용: {}", mailDTO.getMessage());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDTO.getAddress());
        message.setSubject(mailDTO.getTitle());
        message.setText(mailDTO.getMessage());
        javaMailSender.send(message);
    }
}
