package com.cinemax.service.bussines;

import com.cinemax.entity.concretes.business.ShowTime;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.mappers.ShowTimeMapper;
import com.cinemax.payload.response.business.ShowTimeResponse;
import com.cinemax.repository.businnes.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowTimeService {

    private final ShowTimeRepository showTimeRepository;
    private final ShowTimeMapper showTimeMapper;

    public ShowTimeResponse getDetails(Long id) {
        ShowTime s = showTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShowTime not found with id=" + id));
        return showTimeMapper.mapShowTimeToResponse(s);
    }
}

