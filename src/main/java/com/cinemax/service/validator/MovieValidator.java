package com.cinemax.service.validator;

import com.cinemax.payload.request.business.MovieRequest;
import com.cinemax.repository.businnes.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieValidator {


    private final MovieRepository movieRepository;



}
