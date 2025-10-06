package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.payload.request.business.ShowTimeRequest;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.repository.businnes.ShowTimeRepository;
import com.cinemax.service.helper.ShowTimeHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeHelper showTimeHelper;
    private final ShowTimeMapper showTimeMapper;

    public ShowTimeResponse getDetails(Long id) {
        ShowTime s = showTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShowTime not found with id=" + id));
        return showTimeMapper.mapShowTimeToResponse(s);
    }
}

    @Transactional
    public ShowTimeResponse createShowTime(ShowTimeRequest request) {
        // Movie ve Hall bulunur
        var movie = showTimeHelper.getMovieById(request.getMovieId());
        var hall = showTimeHelper.getHallById(request.getHallId());

        // Çakışma kontrolü
        if (showTimeRepository.existsByHall_IdAndDateAndStartTime(
                hall.getId(), request.getDate(), request.getStartTime())) {
            throw new IllegalArgumentException(ErrorMessages.SHOWTIME_CONFLICT);
        }

        // ShowTime entity'si mapper ile oluşturulur
        ShowTime showTime = showTimeMapper.toEntity(request, movie, hall);

        showTimeRepository.save(showTime);

        // Response döndür
        return showTimeMapper.toResponse(showTime);
    }





}
