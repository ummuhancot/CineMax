package com.cinemax.service;
import com.cinemax.exception.EmailSendingException;
import com.cinemax.payload.utils.MessageUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {

    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;
    private final MessageUtil messageUtil;



    //properties'ten hangi freemarker sablonunu/hangi template i kullanacagini belirlemis oluyoruz
    @Value("${email.account.forgot.template}")
    private String emailTemplate;

    // E-postada kullanılacak resmin yolunu properties dosyasından alır --> resim yoksa hata vermez
    @Value("${email.account.forgot.email-img}")
    private String mailImage;

    // application.properties dosyasından e-postanın konu (subject) bilgisini alır.
    // Böylece konu kodun içinde sabitlenmiş olmaz, gerektiğinde config dosyasından kolayca değiştirilebilir
    @Value("${email.account.forgot.subject}")
    private String emailSubject;


    //String email i al, kullanıcıya özel resetPasswordCode alıyor --> kullaniciya e-posta gönderir
    //“Kullanıcıya mail at, içinde reset kodu olsun.”
    public void sendResetPasswordEmail(String recipientEmail, String resetCode) {

        try{
            //Create a new MimeMessage
            MimeMessage message = mailSender.createMimeMessage();//gönderilecek mailin taslağını oluştur

            //Create a new MimeMessageHelper and set the necessary properties
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, // boş MimeMessage - bu helper üzerinden doldurulacak
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,//mailin çok parçalı olmasını sağlar
                    StandardCharsets.UTF_8.name()//UTF-8 seti kullanılacak. Türkçe karakterler düzgün görünsün diye
            );

            //Get the email template
            /*Bu satırın yaptığı: Freemarker motoruna git ve belirtilen şablon dosyasını yükle,
            sonra üzerine veri koyarak email içeriğini oluşturabilirsin. template dosyasının üzerinde islem yapmaya olanak saglar*/
            Template template = freemarkerConfig.getTemplate(emailTemplate);

            // Create a StringWriter to store the processed template
            /*Java Writer sınıfından türer StringWriter. veriyi hafizada bir String olarak saklar*/
            StringWriter writer = new StringWriter();

            //Set the data model for the template
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("code", resetCode);
            dataModel.put("imgSource", mailImage);

            // Process the template with the data model and store the result in the StringWriter
            template.process(dataModel, writer);// Freemarker şablonunu verilen veri modeli ile işler ve sonucu StringWriter içine yazar
            String emailContent = writer.toString();// StringWriter'daki işlenmiş şablon içeriğini bir String olarak alır, e-posta gövdesi olarak kullanılır

            // Set the recipient email address and subject of the email
            // Alıcı adresini ve e-postanın konu başlığını ayarlar
            helper.setTo(recipientEmail);
            helper.setSubject(emailSubject);

            // Set the email content as HTML and send the email
            helper.setText(emailContent, true);
            mailSender.send(message);


        }catch (MessagingException | IOException | TemplateException e){
            //If there is an error sending the email, throw an EmailSendingException with the appopriate message
            throw new EmailSendingException(
                    "Şifre sıfırlama maili gönderilemedi: " + e.getMessage()
            );


        }
    }
}
