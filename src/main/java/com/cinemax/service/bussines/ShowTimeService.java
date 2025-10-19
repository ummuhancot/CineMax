package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.Hall;
import com.cinemax.entity.concretes.business.Movie;
import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.helper.ShowTimeHelper;
import com.cinemax.service.validator.ShowTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        // Movie ve Hall kontrolü
        Movie movie = showTimeHelper.getMovieOrThrow(request.getMovieId());
        Hall hall = showTimeHelper.getHallOrThrow(request.getHallId());

        // Validator ile seans çakışmasını kontrol et
        showTimeValidator.checkOverlap(hall.getId(), request.getDate(), request.getStartTime(), request.getEndTime());

        // Request → Entity (mapper LocalDateTime oluşturuyor)
        ShowTime showTime = showTimeMapper.toEntity(request, movie, hall);

        // Kaydet
        showTime = showTimeRepository.save(showTime);

        // Entity → Response
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


    public ShowTimeResponse getShowTimeById(Long id) {
        ShowTime showTime = showTimeHelper.getShowTimeOrThrow(id);
        return showTimeMapper.toResponse(showTime);
    }

    @Transactional
    public ShowTimeResponse updateShowTime(Long id, ShowTimeRequest request) {
        ShowTime existing = showTimeHelper.getShowTimeOrThrow(id);
        Movie movie = showTimeHelper.getMovieOrThrow(request.getMovieId());
        Hall hall =showTimeHelper.getHallOrThrow(request.getHallId());
        showTimeValidator.checkOverlap(hall.getId(), request.getDate(), request.getStartTime(), request.getEndTime());
        showTimeMapper.updateEntityFromRequest(existing, request, movie, hall);
        ShowTime updated = showTimeRepository.save(existing);
        return showTimeMapper.toResponse(updated);
    }

    @Transactional
    public ShowTimeResponse deleteShowTime(Long id) {
        ShowTime existing = showTimeHelper.getShowTimeOrThrow(id);
        showTimeRepository.delete(existing);
        return showTimeMapper.toResponse(existing);
    }

    @Transactional(readOnly = true)
    public List<ShowTimeResponse> getAllShowTimes() {
        List<ShowTime> showTimes = showTimeRepository.findAll();
        return showTimes.stream()
                .map(showTimeMapper::toResponse)
                .toList();
    }



}
