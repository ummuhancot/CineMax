package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.helper.ShowTimeHelper;
import com.cinemax.service.validator.ShowTimeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeHelper showTimeHelper;
    private final ShowTimeMapper showTimeMapper;
    private final ShowTimeValidator showTimeValidator;

    @Transactional
    public ShowTimeResponse createShowTime(ShowTimeRequest request) {

        Movie movie = showTimeHelper.getMovieOrThrow(request.getMovieId());
        Hall hall = showTimeHelper.getHallOrThrow(request.getHallId());

        // request içindeki LocalDateTime'ları LocalTime'a çevir
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        // validator ile çakışma kontrolü
        showTimeValidator.checkOverlap(hall.getId(), request.getDate(), startTime, endTime);

        // entity oluştur
        ShowTime showTime = showTimeMapper.toEntity(request, movie, hall);
        showTime.setStartTime(startTime);
        showTime.setEndTime(endTime);

        // kaydet
        showTimeRepository.save(showTime);

        return showTimeMapper.toResponse(showTime);
    }


    // Çoklu seans ekleme methodu
    public List<ShowTimeResponse> createMultipleShowTimes(List<ShowTimeRequest> requests) {
        List<ShowTimeResponse> responses = new ArrayList<>();

        for (ShowTimeRequest request : requests) {
            responses.add(createShowTime(request));  // tek tek ekle ve response al
        }

        return responses;
    }


//    public List<ShowTimeResponse> getShowTimesByMovie(Long movieId) {
//        List<ShowTime> showTimes = showTimeRepository.findByMovieId(movieId);
//        return showTimes.stream()
//                .map(showTimeMapper::toResponse)
//                .toList();
//    }

    public ShowTimeResponse getShowTimeById(Long id) {
        ShowTime showTime = showTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShowTime not found with id: " + id));

        return showTimeMapper.mapShowTimeToResponse(showTime);
    }



}
