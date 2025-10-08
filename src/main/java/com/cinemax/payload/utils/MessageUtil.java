package com.cinemax.payload.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    // Key'e göre mesaj alır, default mesaj yoksa "Error: Message not found" döner
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, "Error: Message not found", LocaleContextHolder.getLocale());
    }

    // Argümanlı mesaj alır
    public String getMessage(String key, Object[] args) {
        return getMessage(key, args, "Error: Message not found", LocaleContextHolder.getLocale());
    }

    // Argümanlı ve varsayılan mesaj ile alır
    public String getMessage(String key, Object[] args, String defaultMessage) {
        return getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    // Argümanlı, varsayılan mesaj ve locale ile alır
    public String getMessage(String key, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }
}
