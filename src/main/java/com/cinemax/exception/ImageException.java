package com.cinemax.exception;
/**
 * Resim (image) işlemleri sırasında oluşabilecek hataları temsil eden özel exception sınıfıdır.
 * <p>
 * RuntimeException sınıfından türetilmiştir, bu nedenle checked exception değildir.
 * Yani, try-catch bloğu içinde yakalanması zorunlu değildir.
 */
public class ImageException extends RuntimeException {

    // Sadece hata mesajını alır.
    // Örneğin: throw new ImageException("Image file is empty");

    public ImageException(String message) {
        super(message);// Üst sınıf olan RuntimeException'ın constructor'ına mesajı iletir.
    }

    // Hata mesajı ile birlikte hatanın kök nedenini (cause) de alır.
    // Örneğin: throw new ImageException("Failed to read image", e);
    public ImageException(String message, Throwable cause){
        super(message, cause);// Üst sınıfa hem mesajı hem de hatanın nedenini gönderir.
    }
}
