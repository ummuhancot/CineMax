package com.cinemax.payload.messages;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessages {

    //userr
    // user - auth & registration & password
    public static final String LOGIN_FAILED = "Invalid username or password."; // Geçersiz kullanıcı adı veya şifre
    public static final String REGISTER_VALIDATION_FAILED = "User registration failed. Please check email, phone or password format."; // Kullanıcı kaydı başarısız. Lütfen e-posta, telefon veya şifre formatını kontrol edin.
    public static final String FORGOT_PASSWORD_EMAIL_NOT_FOUND = "No account found with the provided email."; // Verilen e-posta ile hesap bulunamadı
    public static final String RESET_PASSWORD_OLD_PASSWORD_MISMATCH = "Old password does not match."; // Eski şifre eşleşmiyor
    public static final String RESET_PASSWORD_RECENT_PASSWORD_MATCH = "The new password cannot be the same as any of the last 3 passwords."; // Yeni şifre son 3 şifre ile aynı olamaz
	public static final String EMAIL_ALREADY_EXISTS = "Email already exists for another user";
	public static final String PHONE_ALREADY_EXISTS = "Phone already exists for another user";
	public static final String ROLE_NOT_FOUND = "Role: %s not found";
    public static final String USER_ALREADY_EXISTS = "User already exists: %s";

    // user - permissions & role
    public static final String NOT_HAVE_EXPECTED_ROLE_USER = "Error: User does not have expected role"; // Kullanıcının beklenen rolü yok
    public static final String NOT_PERMITTED_METHOD_MESSAGE = "You do not have any permission to do this operation"; // Bu işlemi yapmaya izniniz yok

    // user - CRUD
    public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found with id %s"; // %s id'sine sahip kullanıcı bulunamadı
    public static final String NOT_FOUND_USER_MESSAGE_USERNAME = "Error: User not found with username %s"; // %s kullanıcı adına sahip kullanıcı bulunamadı
    public static final String USERS_FETCH_FAILED = "Failed to fetch users."; // Kullanıcılar getirilemedi
    public static final String USER_NOT_FOUND_ID = "User not found with the provided id %s ."; // Verilen id ile kullanıcı bulunamadı
    public static final String USER_NOT_FOUND_MAIL = "User not found with the provided mail %s .";
    public static final String AUTH_USER_UPDATE_FORBIDDEN = "Built-in users cannot be updated."; // Dahili kullanıcılar güncellenemez
    public static final String USER_UPDATE_FORBIDDEN = "This user cannot be updated (built-in restriction or insufficient role)."; // Bu kullanıcı güncellenemez (dahili kısıtlama veya yetersiz rol)
    public static final String AUTH_USER_DELETE_FORBIDDEN = "User with unused tickets cannot be deleted."; // Kullanıcının kullanılmamış biletleri varsa silinemez
    public static final String USER_DELETE_FAILED = "User deletion failed."; // Kullanıcı silme işlemi başarısız
	public static final String USER_NOT_FOUND_WITH_QUERY =  "User not found with query: %s at the name, surname, email or phone number" ;

    public static final String USER_BUILT_IN = "The user is built-in (system-defined) and cannot be deleted.";

	public static final String USER_DELETE_FORBIDDEN = "This user cannot be deleted (built-in restriction or insufficient role).";
	public static final String USER_DELETE_SELF_FORBIDDEN = "You cannot delete your own account.";

    //movie
    public static final String MOVIES_QUERY_FAILED = "Failed to retrieve movies based on query."; // Sorguya göre filmler getirilemedi. (T-1 & T-6)
    public static final String MOVIES_MARMARA_PARK_FAILED = "Failed to retrieve movies for Marmara Park cinema."; // Marmara Park sinemasındaki filmler getirilemedi. (T-2)
    public static final String MOVIES_IMAX_FAILED = "Failed to retrieve movies for IMAX halls."; // IMAX salonlarındaki filmler getirilemedi. (T-3)
    public static final String IN_THEATER_MOVIES_FAILED = "Failed to retrieve in-theater movies."; // Sinemalarda gösterimde olan filmler getirilemedi. (T-4)
    public static final String UPCOMING_MOVIES_FAILED = "Failed to retrieve upcoming movies."; // Yakında vizyona girecek filmler getirilemedi. (T-5)
    public static final String MOVIE_NOT_FOUND = "Movie not found."; // Film bulunamadı. (T-7)
    public static final String MOVIE_NOT_FOUND_ADMIN = "Movie not found for admin."; // Film (admin için) bulunamadı. (T-8)
    public static final String MOVIE_CREATE_FAILED = "Failed to create movie."; // Film oluşturulamadı. (T-9)
    public static final String MOVIE_UPDATE_FAILED = "Failed to update movie."; // Film güncellenemedi. (T-10)
    public static final String MOVIE_DELETE_FAILED = "Failed to delete movie."; // Film silinemedi. (T-11)
    public static final String SHOWTIMES_FAILED = "Failed to retrieve showtimes."; // Film seans saatleri getirilemedi. (T-12)
    public static final String SHOWTIMES_PAST_NOT_DISPLAYED = "Past showtimes are not displayed."; // Önceki tarih ve saatlerin gösterim saatleri görüntülenmez. (T-12)

    public static final String POSTER_NOT_FOUND = "Movie update failed: Poster not found.";
    public static final String SLUG_ALREADY_EXISTS = "Movie update failed: Slug already exists.";

    //Image
    public static final String IMAGE_NOT_FOUND = "Image not found."; // Görüntü bulunamadı. (T-1)
    public static final String IMAGE_UPLOAD_FAILED = "Failed to upload image."; // Görüntü yüklenemedi. (T-2)
    public static final String IMAGE_DELETE_FAILED = "Failed to delete image."; // Görüntü silinemedi. (T-3)
    public static final String IMAGE_UPDATE_FAILED = "Failed to update image."; // Görüntü güncellenemedi. (T-4)
    public static final String MOVIE_POSTER_REQUIRED = "Movie poster is required";


    //Cinema
    public static final String CINEMAS_QUERY_FAILED = "Failed to retrieve cinemas based on city and special halls."; // Şehir ve özel salonlara dayalı sinemalar getirilemedi. (T-1)
    public static final String FAVORITE_CINEMAS_FAILED = "Failed to retrieve favorite cinemas."; // Kullanıcının favorilerine göre sinemalar getirilemedi. (T-2)
    public static final String CINEMA_NOT_FOUND = "Cinema with id %d not found."; // Sinema salonu bulunamadı. (T-3)
    public static final String CINEMA_HALLS_FAILED = "Failed to retrieve cinema halls."; // Sinema salonları getirilemedi. (T-4)
    public static final String SPECIAL_HALLS_FAILED = "Failed to retrieve special halls."; // Tüm özel salonlar getirilemedi. (T-5)
    public static final String CINEMA_EMAIL_EXISTS = "Cinema with email '%s' already exists.";
    public static final String CINEMA_PHONE_EXISTS = "Cinema with phone number '%s' already exists.";
    public static final String CINEMA_NOT_IN_CITY = "Cinema with id %d does not belong to city %d.";
    public static final String CINEMA_ALREADY_EXISTS_WITH_SLUG = "Cinema already exists with slug: %s";
    public static final String CINEMA_SLUG_EXISTS = "Cinema already exists with slug: %s";
    public static final String CINEMAS_NOT_FOUND = "Cinemas not found. Entered city: %s, special hall: %s";
    public static final String INVALID_HALL_TYPE = "Invalid special hall type: %s";

    // 🔹 Genel hata mesajları
    public static final String BAD_REQUEST_MESSAGE = "Invalid request.";
    public static final String RESOURCE_NOT_FOUND = "Resource not found.";
    public static final String ACCESS_DENIED_MESSAGE = "Access denied.";
    public static final String CINEMA_DUPLICATE_ENTRY = "Cinema with given details already exists.";

    // 🔹 Database & Constraint
    public static final String DUPLICATE_KEY_VIOLATION = "Duplicate key value violates unique constraint.";
    public static final String DATABASE_ERROR = "Unexpected database error occurred.";

    // 🔹 Validation
    public static final String INVALID_INPUT = "Invalid input data.";
    public static final String REQUIRED_FIELD_MISSING = "Required field is missing: %s";

    //ShowTime
    public static final String SHOWTIME_NOT_FOUND = "Showtime not found."; // Gösteri saati bulunamadı. (T-1)
    public static final String SHOWTIME_CONFLICT = "There is already a showtime for this hall at the given time!";

    //Ticket
    public static final String TICKET_NOT_FOUND = "Ticket with id %d not found.";
    public static final String TICKET_CANNOT_BE_PAID = "Bu bilet ödenemez: Ticket durumu uygun değil. (P-2)";
    public static final String CURRENT_TICKETS_FAILED = "Failed to retrieve current tickets."; // Kimliği doğrulanmış kullanıcının kullanmadığı biletler getirilemedi. (T-1)
    public static final String PASSED_TICKETS_FAILED = "Failed to retrieve passed tickets."; // Kimliği doğrulanmış kullanıcının kullandığı biletler getirilemedi. (T-2)
    public static final String RESERVE_TICKET_FAILED = "Failed to reserve ticket."; // Bilet ayıramadı. (T-3)
    public static final String TICKET_CREATE_FAILED = "Failed to create ticket."; // Bilet oluşturulamadı. (T-4)
    public static final String SEAT_ALREADY_RESERVED = "This seat is already reserved or sold.";
    public static final String PAYMENT_FAILED = "Cannot reserve ticket for failed payment.";
    public static final String PAYMENT_STATUS_NULL = "Payment status cannot be null.";

    //City
    public static final String CITY_NOT_FOUND = "City not found";
    public static final String CITY_NAME_CANNOT_BE_EMPTY = "City name cannot be empty";
    public static final String CITY_ALREADY_EXISTS = "City with name '%s' already exists.";
    public static final String CITY_NAME_CANNOT_BE_BLANK = "City name cannot be blank";

    //hall
    public static final String HALL_ALREADY_EXISTS = "Hall already exists with name: %s in this cinema";//Bu sinemada %s isminde bir salon zaten mevcut
    public static final String HALL_NOT_FOUND = "Hall not found. ID: %s";
    public static final String HALL_NOT_FOUND_FOR_SHOWTIME = "Hall not found for showtime";
    public static final String SEATS_NOT_LOADED = "No seats loaded for hallId=%d";
    public static final String NO_AVAILABLE_SEATS = "No available seats for hallId=%d";
    public static final String SEAT_NOT_SELECTED = "At least one seat must be selected.";
    public static final String INVALID_SEAT_FORMAT = "Invalid seat format: ";


    //favorite
    public static final String USER_NOT_FOUND = "User not found with email: %s";
    public static final String MOVIE_NOT_IN_FAVORITES = "The movie is not in user's favorites.";
    public static final String FAVORITE_NOT_OWNED_BY_USER = "This favorite does not belong to the user";
    public static final String FAVORITE_NOT_FOUND = "Favorite not found";
    public static final String FAVORITE_ALREADY_EXISTS = "This movie in this cinema is already in favorites";


    //payment
    public static final String PAYMENT_NOT_FOUND = "Payment not found with the given id.";
    public static final String INSUFFICIENT_PAYMENT = "Yetersiz ödeme: Eksik tutar. (P-1)";


    //Utility class
    public static final String UTILITY_CLASS_SHOULD_NOT_BE_INSTANTIATED = "Utility class — cannot be instantiated";

}
