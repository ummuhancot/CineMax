package com.cinemax.payload.request.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordRequest {

    @NotNull(message = "{validation.email.null}")
    @Email(message = "validation.email.pattern")
    private String email;
}

/**
 *[Kullanıcı]
 *    |
 *    | 1️⃣ "Forgot Password" isteği (email girer)
 *    v
 * [AuthController / UserController]
 *    | (isteği alır)
 *    v
 * [UserService / AuthService]
 *    | (kullanıcı var mı kontrol edilir)
 *    v
 * [UserRepository] -----------\
 *    | (veritabanında email ile kullanıcı aranır)
 *    \------------------------/
 *    |
 *    | 2️⃣ Kullanıcı varsa UUID reset kodu oluşturulur
 *    v
 * [UUID.randomUUID().toString()] -> resetCode
 *    |
 *    | 3️⃣ Kullanıcıya resetCode kaydedilir
 *    v
 * [user.setResetPasswordCode(resetCode)]
 * [userRepository.save(user)]
 *    |
 *    | 4️⃣ EmailService çağrılır
 *    v
 * [EmailService.sendResetPasswordEmail(user.getEmail(), resetCode)]
 *    |
 *    | 5️⃣ EmailService JavaMailSender ile mail gönderir
 *    v
 * [EmailConfig / JavaMailSender] -> SMTP sunucusuna mail gönderilir
 *    |
 *    v
 * [Kullanıcının Mail Kutusu] -> Kullanıcı link veya kodu alır
 * [Kullanıcı]
 *    | 6️⃣ Maildeki linke tıklar veya kodu girer
 *    v
 * [AuthController / UserController] -> reset code kontrol edilir
 *    |
 *    | 7️⃣ UserService kontrol eder: kod eşleşiyor mu ve süresi dolmadı mı?
 *    v
 * [Kullanıcı doğrulandı]
 *    |
 *    | 8️⃣ Kullanıcı yeni şifresini girer
 *    v
 * [user.setPassword(newHashedPassword)]
 * [user.setResetPasswordCode(null)]  -> reset kodu silinir
 * [userRepository.save(user)]
 *    |
 *    v
 * [Kullanıcı artık yeni şifresiyle giriş yapabilir]

 //-----------------------------------
 * 🔹 Akış Özet
 *
    1 - Kullanıcı email gönderir.
 *
    2 - UUID ile benzersiz reset kodu oluşturulur.
 *
    3 - Kod kullanıcıya kaydedilir ve mail ile gönderilir.
 *
    4 - Kullanıcı maili alır, link veya kod ile doğrulama yapar.
 *
    5 - Yeni şifre girilir ve kod geçersiz hale getirilir.
 */