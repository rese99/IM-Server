package com.yun.im.utilies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom(String.valueOf(new InternetAddress("IM<"+"w2103yy@sohu.com"+">")));
            message.setTo(to);
            message.setSubject(content);
            message.setText(subject);
            javaMailSender.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }
}
