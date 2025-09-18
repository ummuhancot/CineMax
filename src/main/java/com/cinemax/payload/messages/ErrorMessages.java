package com.cinemax.payload.messages;

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

}
