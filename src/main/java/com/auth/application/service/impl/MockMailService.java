package com.auth.application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("dev") // chỉ chạy khi profile là 'dev'
public class MockMailService extends MailService{
    @Override
    public void sendMail(String to, String subject, String content) {

        System.out.println("---- MOCK EMAIL ----");
        System.out.println("TO      : " + to);
        System.out.println("SUBJECT : " + subject);
        System.out.println("CONTENT : " + content);
        System.out.println("--------------------");
    }

}
