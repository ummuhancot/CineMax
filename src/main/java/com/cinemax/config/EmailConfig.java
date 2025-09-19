package com.cinemax.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration // Bu class, Spring için bir configuration class olduğunu belirtir
public class EmailConfig {

    @Value("${spring.mail.host}") // application.properties içindeki  değerini alır
    private String host;

    @Value("${spring.mail.port}") // Mail sunucu portu (genelde 587, 465 gibi)
    private int port;

    @Value("${spring.mail.username}") // Mail kullanıcı adı (örn: cine.max.app.noreply@gmail.com)
    private String username;//application-mail.properties e eklendi

    @Value("${spring.mail.password}") // Mail şifresi (App Password olacak genelde)
    private String password;//application-mail.properties e eklendi

    @Value("${spring.mail.properties.mail.smtp.auth}") // SMTP için authentication zorunlu mu?
    private String mailSmtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}") // TLS güvenlik protokolü açık mı?
    private String mailSmtpStartTls;

    @Value("${spring.mail.default-encoding}") // Mail encoding (genelde UTF-8)
    private String mailEncoding;

    @Value("${spring.mail.protocol}") // Mail protokolü (smtp)
    private String protocol;


    @Bean // Bu method Spring context içine JavaMailSender Bean’i tanımlar
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Yukarıda properties’den aldığımız değerleri set ediyoruz
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setDefaultEncoding(mailEncoding);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setProtocol(protocol);

        // Ek özellikler (authentication, TLS, debug gibi)
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailSmtpStartTls);
        props.put("mail.debug", "true"); // Konsola mail loglarını yazdırır

        return mailSender; // Spring bu objeyi kullanarak mail gönderecek
    }
}


/**
 [Kullanıcı]
 |
 | 1️⃣ "Forgot Password" isteği (email girer)
 v
 [AuthController / UserController]
 | (isteği alır)
 v
 [AuthenticationService / UserService]
 | (kullanıcı var mı kontrol edilir)
 v
 [UserRepository] -----------\
 | (veritabanında email ile kullanıcı aranır)
 \------------------------/
 |
 | 2️⃣ Kullanıcı varsa UUID reset kodu oluşturulur
 v
 [UUID.randomUUID().toString()] -> resetCode
 |
 | 3️⃣ Kullanıcıya resetCode kaydedilir
 v
 [user.setResetPasswordCode(resetCode)]
 [userRepository.save(user)]
 |
 | 4️⃣ EmailService çağrılır
 v
 [EmailService.sendResetPasswordEmail(user.getEmail(), resetCode)]
 |   (mailSender inject edilmiş)
 |
 | 5️⃣ JavaMailSender kullanılır
 v
 [EmailConfig.getJavaMailSender()] -> SMTP ayarları uygulanır
 | (host, port, auth, TLS, encoding, protocol)
 v
 [SMTP Sunucusu] -> mail gönderilir
 |
 v
 [Kullanıcının Mail Kutusu] -> Kullanıcı reset link veya kodu alır

 */