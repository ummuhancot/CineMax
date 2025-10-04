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

    //movie
    public static final String MOVIES_QUERY_SUCCESS = "Movies retrieved successfully based on query and paging parameters."; // Sorgu ve sayfalama parametrelerine göre filmler başarıyla getirildi. (T-1 & T-6)
    public static final String MOVIES_MARMARA_PARK_SUCCESS = "Movies retrieved successfully for Marmara Park cinema."; // Marmara Park sinemasındaki filmler başarıyla getirildi. (T-2)
    public static final String MOVIES_IMAX_SUCCESS = "Movies retrieved successfully for IMAX halls."; // IMAX salonlarındaki filmler başarıyla getirildi. (T-3)
    public static final String IN_THEATER_MOVIES_SUCCESS = "Currently in-theater movies retrieved successfully."; // Sinemalarda gösterimde olan filmler başarıyla getirildi. (T-4)
    public static final String UPCOMING_MOVIES_SUCCESS = "Upcoming movies retrieved successfully."; // Yakında vizyona girecek filmler başarıyla getirildi. (T-5)
    public static final String MOVIE_DETAILS_SUCCESS = "Movie details retrieved successfully."; // Film detayları başarıyla getirildi. (T-7)
    public static final String MOVIE_ADMIN_SUCCESS = "Movie retrieved successfully for admin."; // Film (admin için) başarıyla getirildi. (T-8)
    public static final String MOVIE_CREATE_SUCCESS = "Movie created successfully."; // Film başarıyla oluşturuldu. (T-9)
    public static final String MOVIE_UPDATE_SUCCESS = "Movie updated successfully."; // Film başarıyla güncellendi. (T-10)
    public static final String MOVIE_DELETE_SUCCESS = "Movie deleted successfully."; // Film başarıyla silindi. (T-11)
    public static final String SHOWTIMES_SUCCESS = "Showtimes retrieved successfully."; // Film seans saatleri başarıyla getirildi. (T-12)

    //Image
    public static final String IMAGE_RETRIEVE_SUCCESS = "Image retrieved successfully."; // (T-1)
    public static final String IMAGE_UPLOAD_SUCCESS = "Image uploaded successfully."; // (T-2)
    public static final String IMAGE_DELETE_SUCCESS = "Image deleted successfully."; // (T-3)
    public static final String IMAGE_UPDATE_SUCCESS = "Image updated successfully."; // (T-4)

    //Cinema
    public static final String CINEMAS_QUERY_SUCCESS = "Cinemas retrieved successfully based on city and special halls."; // Şehir ve özel salonlara dayalı sinemalar başarıyla getirildi. (T-1)
    public static final String FAVORITE_CINEMAS_SUCCESS = "Favorite cinemas retrieved successfully."; // Kullanıcının favorilerine göre sinemalar başarıyla getirildi. (T-2)
    public static final String CINEMA_DETAILS_SUCCESS = "Cinema details retrieved successfully."; // Sinema salonu ayrıntıları başarıyla getirildi. (T-3)
    public static final String CINEMA_HALLS_SUCCESS = "Cinema halls retrieved successfully."; // Sinema salonları başarıyla getirildi. (T-4)
    public static final String SPECIAL_HALLS_SUCCESS = "Special halls retrieved successfully."; // Tüm özel salonlar başarıyla getirildi. (T-5)

    //ShowTime
    public static final String SHOWTIME_DETAILS_SUCCESS = "Showtime details retrieved successfully."; // Gösteri saatinin ayrıntıları başarıyla getirildi. (T-1)

    //Ticket
    public static final String CURRENT_TICKETS_SUCCESS = "Current tickets retrieved successfully."; // Kimliği doğrulanmış kullanıcının kullanmadığı biletler başarıyla getirildi. (T-1)
    public static final String PASSED_TICKETS_SUCCESS = "Passed tickets retrieved successfully."; // Kimliği doğrulanmış kullanıcının kullandığı biletler başarıyla getirildi. (T-2)
    public static final String RESERVE_TICKET_SUCCESS = "Ticket reserved successfully."; // Bilet başarıyla ayrıldı. (T-3)
    public static final String TICKET_CREATE_SUCCESS = "Ticket created successfully."; // Bilet başarıyla oluşturuldu. (T-4)

    //favorite
    public static final String FAVORITE_MOVIE_ADDED = "User '%s' has added '%s' to favorites successfully!";
    public static final String FAVORITE_MOVIE_REMOVED = "'%s' has been removed from your favorites successfully!";

}
