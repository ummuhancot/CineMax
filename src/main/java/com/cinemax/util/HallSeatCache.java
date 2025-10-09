package com.cinemax.util;

import com.cinemax.payload.messages.ErrorMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class HallSeatCache {

    private HallSeatCache() {
        throw new UnsupportedOperationException(ErrorMessages.UTILITY_CLASS_SHOULD_NOT_BE_INSTANTIATED);
    }

    // Hall ID -> Seat listesi
    private static final Map<Long, List<String>> hallSeats = new ConcurrentHashMap<>();

    /**
     * Hall için seat listesini ekler veya günceller
     */
    public static void addSeats(Long hallId, List<String> seats) {
        hallSeats.put(hallId, seats);
    }

    /**
     * Hall ID'ye göre seat listesini döner
     * Eğer yoksa boş liste döner
     */
    public static List<String> getSeats(Long hallId) {
        return hallSeats.getOrDefault(hallId, new ArrayList<>());
    }

    /**
     * Hall ID cache'de var mı kontrol eder
     */
    public static boolean hasHall(Long hallId) {
        return hallSeats.containsKey(hallId);
    }
}


