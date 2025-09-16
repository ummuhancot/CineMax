package com.cinemax.payload.messages;

public class ErrorMessages {

    //user
    public static final String LOGIN_FAILED = "Invalid username or password.";
    public static final String REGISTER_VALIDATION_FAILED = "User registration failed. Please check email, phone or password format.";
    public static final String FORGOT_PASSWORD_EMAIL_NOT_FOUND = "No account found with the provided email.";
    public static final String RESET_PASSWORD_OLD_PASSWORD_MISMATCH = "Old password does not match.";
    public static final String AUTH_USER_UPDATE_FORBIDDEN = "Built-in users cannot be updated.";
    public static final String AUTH_USER_DELETE_FORBIDDEN = "User with unused tickets cannot be deleted.";
    public static final String USERS_FETCH_FAILED = "Failed to fetch users.";
    public static final String USER_NOT_FOUND = "User not found with the provided id.";
    public static final String USER_UPDATE_FORBIDDEN = "This user cannot be updated (built-in restriction or insufficient role).";
    public static final String USER_DELETE_FAILED = "User deletion failed.";

}
