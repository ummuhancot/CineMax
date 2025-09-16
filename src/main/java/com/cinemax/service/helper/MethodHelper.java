package com.cinemax.service.helper;

import com.cinemax.entity.concretes.user.User;
import com.cinemax.exception.ResourceNotFoundException;
import com.cinemax.payload.messages.ErrorMessages;
import com.cinemax.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MethodHelper {

    private final UserRepository userRepository;


    public User loadByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, username));
        }
        return user;
    }
}
