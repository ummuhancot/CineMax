package com.cinemax.payload.messages;

public class SuccessMessages {

    //userr
    // user - auth & registration & password
    public static final String LOGIN_SUCCESS = "Login successful. Authentication token generated."; // Giriş başarılı. Yetkilendirme belirteci oluşturuldu
    public static final String REGISTER_SUCCESS = "User registered successfully."; // Kullanıcı başarıyla kaydedildi
    public static final String FORGOT_PASSWORD_CODE_SENT = "Password reset code has been sent to your email."; // Şifre sıfırlama kodu e-posta adresinize gönderildi
    public static final String RESET_PASSWORD_SUCCESS = "Password has been reset successfully."; // Şifre başarıyla sıfırlandı (T-4)

    // user - authenticated user (auth)
    public static final String AUTH_USER_CREATED_SUCCESS = "Authenticated user created successfully."; // Kimliği doğrulanmış kullanıcı başarıyla oluşturuldu (T-5)
    public static final String AUTH_USER_UPDATED_SUCCESS = "Authenticated user updated successfully."; // Kimliği doğrulanmış kullanıcı başarıyla güncellendi (T-6)
    public static final String AUTH_USER_DELETED_SUCCESS = "Authenticated user deleted successfully."; // Kimliği doğrulanmış kullanıcı başarıyla silindi (T-7)
    public static final String USER_AUTH_FETCHED_SUCCESS = "Authenticated user fetched successfully."; // Kimliği doğrulanmış kullanıcı başarıyla döndürüldü (T-5 ve T-9) // ✅ Eklenen

    // user - CRUD / admin operations
    public static final String USERS_FETCHED_SUCCESS = "Users fetched successfully."; // Kullanıcılar başarıyla getirildi (T-8)
    public static final String USER_FETCHED_SUCCESS = "User fetched successfully."; // Kullanıcı başarıyla getirildi (T-9) // ✅ Eklenen
    public static final String USER_UPDATED_SUCCESS = "User updated successfully."; // Kullanıcı başarıyla güncellendi (T-10)
    public static final String USER_DELETED_SUCCESS = "User deleted successfully."; // Kullanıcı başarıyla silindi (T-11)



}
