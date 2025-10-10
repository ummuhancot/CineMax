package com.cinemax.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HallSeatCache {

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


