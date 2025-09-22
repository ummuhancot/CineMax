package com.cinemax;

import com.cinemax.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PasswordCheckTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testAdminPassword() {
        // Kullanıcının raw şifresi
        String rawPassword = "Admin123!";

        // DB’den encode edilmiş şifreyi al
        String encodedPassword = userRepository.findByEmail("admin@example.com").get().getPassword();

        // PasswordEncoder ile karşılaştır
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        System.out.println("Password matches: " + matches);

        // Testin doğru çalıştığını assert ile kontrol et
        assertTrue(matches, "Şifre eşleşmiyor! Encode veya DB kaydını kontrol et.");
    }
}
